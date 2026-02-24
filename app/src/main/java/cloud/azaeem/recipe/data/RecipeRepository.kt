package cloud.azaeem.recipe.data

import cloud.azaeem.recipe.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
}
