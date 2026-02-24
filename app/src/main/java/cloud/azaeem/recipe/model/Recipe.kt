package cloud.azaeem.recipe.model

data class Recipe(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val ingredientsList: List<String> = emptyList(),
    val stepsList: List<String> = emptyList(),
    val category: String = "",
    val videoUrl: String? = null,
    val imageUrl: String? = null
)
