package cloud.azaeem.recipe.viewmodel

import android.util.Patterns
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
import java.net.URLEncoder
import java.util.UUID

sealed class AddEditUiState {
    data object Idle : AddEditUiState()
    data object Loading : AddEditUiState()
    data object Success : AddEditUiState()
    data class Error(val message: String) : AddEditUiState()
}

class AddEditRecipeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

    private var currentRecipeId: String? = null

    val categories = listOf(
        "Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Vegan", "Keto", "Appetizer", "Soup",
        "Salad", "Main Course", "Side Dish", "Beverage", "Bakery", "Italian", "Mexican",
        "Chinese", "Indian", "Japanese", "Thai", "Vietnamese", "Greek", "French", "Spanish",
        "Middle Eastern", "Lebanese", "Moroccan", "Egyptian", "Turkish", "Saudi"
    )

    fun loadRecipe(recipeId: String) {
        if (recipeId.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val document = db.collection("recipes").document(recipeId).get().await()
                val recipe = document.toObject(Recipe::class.java)
                if (recipe != null) {
                    currentRecipeId = recipe.id
                    _title.value = recipe.title
                    _ingredients.value = recipe.ingredientsList.joinToString(", ")
                    _steps.value = recipe.stepsList.joinToString("\n")
                    _category.value = recipe.category
                    _videoUrl.value = recipe.videoUrl ?: ""
                    _uiState.value = AddEditUiState.Idle
                } else {
                    _uiState.value = AddEditUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error(e.message ?: "Failed to load recipe")
            }
        }
    }

    fun onTitleChange(newValue: String) { _title.value = newValue }
    fun onIngredientsChange(newValue: String) { _ingredients.value = newValue }
    fun onStepsChange(newValue: String) { _steps.value = newValue }
    fun onCategoryChange(newValue: String) { _category.value = newValue }
    fun onVideoUrlChange(newValue: String) { _videoUrl.value = newValue }

    private fun getUnsplashImageUrl(query: String): String {
        val q = query.trim().ifBlank { "food" }
        val encoded = URLEncoder.encode("$q food", Charsets.UTF_8.name())
        return "https://source.unsplash.com/1200x900/?$encoded"
    }

    fun submitRecipe() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _uiState.value = AddEditUiState.Error("You must be logged in to perform this action")
            return
        }

        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val ingredientsList = _ingredients.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val stepsList = _steps.value.split(Regex("[,\\n]+")).map { it.trim() }.filter { it.isNotEmpty() }

                val recipe = Recipe(
                    id = currentRecipeId ?: UUID.randomUUID().toString(),
                    creatorId = currentUserId,
                    title = _title.value,
                    ingredientsList = ingredientsList,
                    stepsList = stepsList,
                    category = _category.value,
                    videoUrl = _videoUrl.value.ifBlank { null },
                    imageUrl = getUnsplashImageUrl(_title.value)
                )

                db.collection("recipes").document(recipe.id).set(recipe).await()
                _uiState.value = AddEditUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error(e.message ?: "Failed to save recipe")
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (_title.value.isBlank()) {
            _uiState.value = AddEditUiState.Error("Recipe name is required")
            return false
        }
        if (_ingredients.value.isBlank()) {
            _uiState.value = AddEditUiState.Error("Ingredients are required")
            return false
        }
        if (_steps.value.isBlank()) {
            _uiState.value = AddEditUiState.Error("Instructions are required")
            return false
        }
        if (_category.value.isBlank()) {
            _uiState.value = AddEditUiState.Error("Please select a category")
            return false
        }
        if (_videoUrl.value.isNotBlank() && !Patterns.WEB_URL.matcher(_videoUrl.value).matches()) {
            _uiState.value = AddEditUiState.Error("Please enter a valid video URL")
            return false
        }
        return true
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
