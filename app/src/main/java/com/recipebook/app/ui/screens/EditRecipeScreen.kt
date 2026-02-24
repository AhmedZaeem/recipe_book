package com.recipebook.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.app.viewmodel.AddEditRecipeViewModel

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
