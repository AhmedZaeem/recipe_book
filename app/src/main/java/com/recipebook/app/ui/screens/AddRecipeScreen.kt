package com.recipebook.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.app.viewmodel.AddEditRecipeViewModel

@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditRecipeViewModel = viewModel()
) {
    // In a real app, you might scope this ViewModel to the navigation graph
    // or use a shared ViewModel if the data needs to persist across screens
    // For now, a new instance is fine.
    
    RecipeForm(
        screenTitle = "Add Recipe",
        onNavigateBack = onNavigateBack,
        viewModel = viewModel
    )
}
