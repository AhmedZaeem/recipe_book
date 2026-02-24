package com.recipebook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(1500) // Simulate network delay
            if (email.isNotBlank() && pass.isNotBlank()) {
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Invalid email or password")
            }
        }
    }

    fun register(name: String, email: String, pass: String, country: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(2000) // Simulate network delay
            if (name.isNotBlank() && email.isNotBlank() && pass.isNotBlank() && country.isNotBlank()) {
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Please fill all fields")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
