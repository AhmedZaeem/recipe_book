package cloud.azaeem.recipe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cloud.azaeem.recipe.model.Recipe
import cloud.azaeem.recipe.viewmodel.ProfileUiState
import cloud.azaeem.recipe.viewmodel.ProfileViewModel
import cloud.azaeem.recipe.viewmodel.ThemeViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditRecipe: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel(),
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE) }
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    var showEdit by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editCountry by remember { mutableStateOf("") }
    var editPhotoUrl by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.LoggedOut) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { 
                    Text(
                        "MY ACCOUNT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ) 
                },
                actions = {
                    IconButton(
                        onClick = {
                            prefs.edit().putBoolean("remember_me", false).apply()
                            viewModel.logout()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ProfileUiState.Success -> {
                val user = state.user
                val recipes = state.userRecipes
                if (showEdit) {
                    ModalBottomSheet(
                        onDismissRequest = { showEdit = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Edit Profile",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                            OutlinedTextField(
                                value = editCountry,
                                onValueChange = { editCountry = it },
                                label = { Text("Country") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                            OutlinedTextField(
                                value = editPhotoUrl,
                                onValueChange = { editPhotoUrl = it },
                                label = { Text("Photo URL") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                            Button(
                                onClick = {
                                    viewModel.updateProfile(
                                        name = editName.trim(),
                                        country = editCountry.trim(),
                                        photoUrl = editPhotoUrl.trim().ifBlank { null }
                                    )
                                    showEdit = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Save")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clickable {
                                        editName = user.name
                                        editCountry = user.country
                                        editPhotoUrl = user.photoUrl.orEmpty()
                                        showEdit = true
                                    },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                if (user.photoUrl != null) {
                                    AsyncImage(
                                        model = user.photoUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (user.country.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = user.country,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    editName = user.name
                                    editCountry = user.country
                                    editPhotoUrl = user.photoUrl.orEmpty()
                                    showEdit = true
                                },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Edit Profile")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Dark Mode",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = { themeViewModel.toggleTheme() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                        
                        Text(
                            text = "MY COLLECTIONS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(recipes) { recipe ->
                        ProfileRecipeItem(
                            recipe = recipe,
                            onEditClick = { onNavigateToEditRecipe(recipe.id) },
                            onDeleteClick = { viewModel.deleteRecipe(recipe.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ProfileRecipeItem(
    recipe: Recipe,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = recipe.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
