package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe, val isCreator: Boolean) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val document = db.collection("recipes").document(recipeId).get().await()
                val recipe = document.toObject(Recipe::class.java)
                if (recipe != null) {
                    val currentUserId = auth.currentUser?.uid
                    _uiState.value = RecipeDetailUiState.Success(recipe, recipe.creatorId == currentUserId)
                } else {
                    _uiState.value = RecipeDetailUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error("Error loading recipe: ${e.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                db.collection("recipes").document(recipeId).delete().await()
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error("Failed to delete recipe: ${e.message}")
            }
        }
    }
}
