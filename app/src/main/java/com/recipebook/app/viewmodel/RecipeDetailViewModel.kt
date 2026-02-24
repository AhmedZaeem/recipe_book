package com.recipebook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipebook.app.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe, val isCreator: Boolean) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            
            val currentUserId = "mock_user_id"
            
            val mockRecipe = Recipe(
                id = recipeId,
                creatorId = if (recipeId == "1") currentUserId else "other_user",
                title = "Classic Pancakes",
                ingredientsList = listOf("2 cups flour", "2 eggs", "1.5 cups milk", "1 tbsp sugar", "2 tsp baking powder", "1/2 tsp salt", "2 tbsp butter"),
                stepsList = listOf(
                    "Mix dry ingredients in a large bowl.",
                    "Whisk wet ingredients in a separate bowl.",
                    "Combine wet and dry ingredients until just mixed.",
                    "Heat a griddle or pan over medium heat.",
                    "Pour batter onto the griddle and cook until bubbles form.",
                    "Flip and cook until golden brown.",
                    "Serve with syrup and butter."
                ),
                category = "Breakfast",
                videoUrl = "https://www.youtube.com/watch?v=FLd00Bx4tOk",
                imageUrl = "https://example.com/pancakes.jpg"
            )

            _uiState.value = RecipeDetailUiState.Success(
                recipe = mockRecipe,
                isCreator = mockRecipe.creatorId == currentUserId
            )
        }
    }

    fun deleteRecipe(recipeId: String) {
        
    }
}
