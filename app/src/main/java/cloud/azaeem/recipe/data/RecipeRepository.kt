package cloud.azaeem.recipe.data

import cloud.azaeem.recipe.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    suspend fun getRecipe(id: String): Recipe?
    suspend fun createRecipe(recipe: Recipe): Result<Unit>
}
