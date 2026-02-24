package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.Recipe
import cloud.azaeem.recipe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val user: User, val userRecipes: List<Recipe>) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data object LoggedOut : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.value = ProfileUiState.LoggedOut
                return@launch
            }

            _uiState.value = ProfileUiState.Loading
            try {
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                val userData = userDoc.toObject(User::class.java) ?: User(
                    id = currentUser.uid,
                    name = currentUser.displayName ?: "User",
                    email = currentUser.email ?: "",
                    photoUrl = currentUser.photoUrl?.toString()
                )

                val recipesSnapshot = db.collection("recipes")
                    .whereEqualTo("creatorId", currentUser.uid)
                    .get()
                    .await()
                val userRecipes = recipesSnapshot.toObjects(Recipe::class.java)

                _uiState.value = ProfileUiState.Success(user = userData, userRecipes = userRecipes)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Failed to load profile: ${e.message}")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = ProfileUiState.LoggedOut
    }

    fun updateProfile(name: String, country: String, photoUrl: String?) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.value = ProfileUiState.LoggedOut
                return@launch
            }
            _uiState.value = ProfileUiState.Loading
            try {
                val update = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(photoUrl?.takeIf { it.isNotBlank() }?.let { Uri.parse(it) })
                    .build()
                currentUser.updateProfile(update).await()

                val userDoc = mapOf(
                    "id" to currentUser.uid,
                    "name" to name,
                    "email" to (currentUser.email ?: ""),
                    "photoUrl" to photoUrl?.takeIf { it.isNotBlank() },
                    "country" to country
                )
                db.collection("users").document(currentUser.uid).set(userDoc).await()
                loadProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Failed to update profile: ${e.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                db.collection("recipes").document(recipeId).delete().await()
                loadProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Failed to delete recipe: ${e.message}")
            }
        }
    }
}
