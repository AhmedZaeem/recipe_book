package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(mapAuthError(e))
            }
        }
    }

    fun register(name: String, email: String, pass: String, country: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || country.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user ?: throw IllegalStateException("User not available")

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdate).await()

                val userDoc = User(
                    id = user.uid,
                    name = name,
                    email = user.email ?: email,
                    photoUrl = user.photoUrl?.toString(),
                    country = country
                )
                db.collection("users").document(user.uid).set(userDoc).await()

                auth.signOut()
                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(mapAuthError(e))
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun mapAuthError(e: Exception): String {
        val msg = e.message.orEmpty()
        if (msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
            return "Firebase Auth configuration missing for this app. Add your SHA-256 in Firebase Console (Project Settings > Your apps > Android) and download a new google-services.json."
        }
        if (msg.contains("The email address is badly formatted", ignoreCase = true)) {
            return "Invalid email address"
        }
        if (msg.contains("password is invalid", ignoreCase = true) || msg.contains("no user record", ignoreCase = true)) {
            return "Incorrect email or password"
        }
        if (msg.contains("already in use", ignoreCase = true)) {
            return "Email is already registered"
        }
        return if (msg.isNotBlank()) msg else "Authentication failed"
    }
}
