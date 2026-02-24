package cloud.azaeem.recipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.azaeem.recipe.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val categories = listOf(
        "All", "Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Vegan", "Keto", "Appetizer", "Soup",
        "Salad", "Main Course", "Side Dish", "Beverage", "Bakery", "Italian", "Mexican",
        "Chinese", "Indian", "Japanese", "Thai", "Vietnamese", "Greek", "French", "Spanish",
        "Middle Eastern", "Lebanese", "Moroccan", "Egyptian", "Turkish", "Saudi"
    )

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            db.collection("recipes")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val recipes = snapshot.toObjects(Recipe::class.java)
                        _allRecipes.value = recipes
                    }
                }
        }
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
