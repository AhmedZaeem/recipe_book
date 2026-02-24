package cloud.azaeem.recipe.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cloud.azaeem.recipe.viewmodel.AddEditRecipeViewModel

@Composable
fun EditRecipeScreen(
    recipeId: String,
    onNavigateBack: () -> Unit,
    viewModel: AddEditRecipeViewModel = viewModel()
) {
    RecipeForm(
        screenTitle = "Edit Recipe",
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        recipeId = recipeId
    )
}
