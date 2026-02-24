package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Vegan", "Keto")

    init {
        _allRecipes.value = listOf(
            Recipe(id = "1", title = "Pancakes", category = "Breakfast", imageUrl = "https://example.com/pancakes.jpg"),
            Recipe(id = "2", title = "Chicken Salad", category = "Lunch", imageUrl = "https://example.com/salad.jpg"),
            Recipe(id = "3", title = "Spaghetti Bolognese", category = "Dinner", imageUrl = "https://example.com/pasta.jpg"),
            Recipe(id = "4", title = "Chocolate Cake", category = "Dessert", imageUrl = "https://example.com/cake.jpg"),
            Recipe(id = "5", title = "Avocado Toast", category = "Breakfast", imageUrl = "https://example.com/toast.jpg"),
            Recipe(id = "6", title = "Burger", category = "Dinner", imageUrl = "https://example.com/burger.jpg"),
            Recipe(id = "7", title = "Fruit Salad", category = "Snack", imageUrl = "https://example.com/fruit.jpg"),
            Recipe(id = "8", title = "Tofu Stir Fry", category = "Vegan", imageUrl = "https://example.com/tofu.jpg")
        )
    }

    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        _allRecipes,
        _searchQuery,
        _selectedCategory
    ) { recipes, query, category ->
        recipes.filter { recipe ->
            val matchesCategory = category == "All" || recipe.category == category
            val matchesSearch = recipe.title.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}
