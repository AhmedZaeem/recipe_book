package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AddEditUiState {
    data object Idle : AddEditUiState()
    data object Loading : AddEditUiState()
    data object Success : AddEditUiState()
    data class Error(val message: String) : AddEditUiState()
}

class AddEditRecipeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _ingredients = MutableStateFlow("")
    val ingredients: StateFlow<String> = _ingredients.asStateFlow()

    private val _steps = MutableStateFlow("")
    val steps: StateFlow<String> = _steps.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _videoUrl = MutableStateFlow("")
    val videoUrl: StateFlow<String> = _videoUrl.asStateFlow()
    
    // For Edit Mode
    private var currentRecipeId: String? = null

    val categories = listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Vegan", "Keto")

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            if (recipeId.isNotEmpty()) {
                currentRecipeId = recipeId
                _title.value = "Mock Recipe Title"
                _ingredients.value = "Flour, Sugar, Eggs"
                _steps.value = "1. Mix ingredients\n2. Bake"
                _category.value = "Dessert"
                _videoUrl.value = "https://example.com/video"
            }
        }
    }

    fun onTitleChange(newValue: String) { _title.value = newValue }
    fun onIngredientsChange(newValue: String) { _ingredients.value = newValue }
    fun onStepsChange(newValue: String) { _steps.value = newValue }
    fun onCategoryChange(newValue: String) { _category.value = newValue }
    fun onVideoUrlChange(newValue: String) { _videoUrl.value = newValue }

    fun submitRecipe() {
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            
            if (_title.value.isBlank() || _ingredients.value.isBlank() || _steps.value.isBlank() || _category.value.isBlank()) {
                _uiState.value = AddEditUiState.Error("Please fill all required fields")
                return@launch
            }

            kotlinx.coroutines.delay(1000)
            
            val ingredientsList = _ingredients.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val stepsList = _steps.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val recipe = Recipe(
                id = currentRecipeId ?: UUID.randomUUID().toString(),
                title = _title.value,
                ingredientsList = ingredientsList,
                stepsList = stepsList,
                category = _category.value,
                videoUrl = _videoUrl.value
            )

            _uiState.value = AddEditUiState.Success
        }
    }
    
    fun resetState() {
        _uiState.value = AddEditUiState.Idle
        _title.value = ""
        _ingredients.value = ""
        _steps.value = ""
        _category.value = ""
        _videoUrl.value = ""
        currentRecipeId = null
    }
}
