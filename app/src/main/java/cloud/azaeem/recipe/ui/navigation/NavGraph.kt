package cloud.azaeem.recipe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cloud.azaeem.recipe.ui.screens.AddRecipeScreen
import cloud.azaeem.recipe.ui.screens.EditRecipeScreen
import cloud.azaeem.recipe.ui.screens.HomeScreen
import cloud.azaeem.recipe.ui.screens.LoginScreen
import cloud.azaeem.recipe.ui.screens.ProfileScreen
import cloud.azaeem.recipe.ui.screens.RecipeDetailScreen
import cloud.azaeem.recipe.ui.screens.RegisterScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object AddRecipe : Screen("add_recipe")
    data object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "edit_recipe/$recipeId"
    }
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
}

@Composable
fun RecipeBookNavGraph(
    navController: NavHostController,
    themeViewModel: cloud.azaeem.recipe.viewmodel.ThemeViewModel,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAddRecipe = {
                    navController.navigate(Screen.AddRecipe.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToEditRecipe = { recipeId ->
                    navController.navigate(Screen.EditRecipe.createRoute(recipeId))
                },
                themeViewModel = themeViewModel
            )
        }
        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditRecipe.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            EditRecipeScreen(
                recipeId = recipeId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.RecipeDetail.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditRecipe.createRoute(id)) }
            )
        }
    }
}
