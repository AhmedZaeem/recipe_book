package cloud.azaeem.recipe.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cloud.azaeem.recipe.viewmodel.AddEditRecipeViewModel

@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditRecipeViewModel = viewModel()
) {
    RecipeForm(
        screenTitle = "Add Recipe",
        onNavigateBack = onNavigateBack,
        viewModel = viewModel
    )
}
