package cloud.azaeem.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cloud.azaeem.recipe.ui.navigation.RecipeBookNavGraph
import cloud.azaeem.recipe.ui.navigation.Screen
import cloud.azaeem.recipe.ui.theme.RecipeBookTheme
import cloud.azaeem.recipe.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val startDestination = remember {
                val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                val rememberMe = prefs.getBoolean("remember_me", true)
                val auth = FirebaseAuth.getInstance()
                if (!rememberMe && auth.currentUser != null) {
                    auth.signOut()
                }
                if (rememberMe && auth.currentUser != null) Screen.Home.route else Screen.Login.route
            }

            RecipeBookTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    RecipeBookNavGraph(
                        navController = navController,
                        themeViewModel = themeViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
