package com.recipebook.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.app.viewmodel.AddEditRecipeViewModel

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
