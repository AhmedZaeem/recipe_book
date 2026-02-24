package cloud.azaeem.recipe.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val country: String = ""
)
