package com.recipebook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipebook.app.model.Recipe
import com.recipebook.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val user: User, val userRecipes: List<Recipe>) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data object LoggedOut : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            
            val mockUser = User(
                id = "mock_user_id",
                name = "John Doe",
                email = "john.doe@example.com",
                country = "USA"
            )

            val mockRecipes = listOf(
                Recipe(id = "1", title = "Pancakes", category = "Breakfast", imageUrl = "https://example.com/pancakes.jpg"),
                Recipe(id = "5", title = "Avocado Toast", category = "Breakfast", imageUrl = "https://example.com/toast.jpg")
            )

            _uiState.value = ProfileUiState.Success(user = mockUser, userRecipes = mockRecipes)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.LoggedOut
        }
    }

    fun deleteRecipe(recipeId: String) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val updatedList = currentState.userRecipes.filter { it.id != recipeId }
            _uiState.value = currentState.copy(userRecipes = updatedList)
        }
    }
}
