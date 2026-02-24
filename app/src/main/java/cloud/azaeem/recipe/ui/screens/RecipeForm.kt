package cloud.azaeem.recipe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cloud.azaeem.recipe.viewmodel.AddEditRecipeViewModel
import cloud.azaeem.recipe.viewmodel.AddEditUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    screenTitle: String,
    onNavigateBack: () -> Unit,
    viewModel: AddEditRecipeViewModel,
    recipeId: String? = null
) {
    val title by viewModel.title.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val category by viewModel.category.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val categories = viewModel.categories
    var expanded by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            viewModel.loadRecipe(recipeId)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AddEditUiState.Success) {
            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onNavigateBack()
        }
        if (uiState is AddEditUiState.Error) {
            Toast.makeText(context, (uiState as AddEditUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        screenTitle.uppercase(), 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Recipe Details",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = "What's the name?",
                icon = Icons.Default.Restaurant
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onCategoryChange(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Ingredients & Steps",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                value = ingredients,
                onValueChange = viewModel::onIngredientsChange,
                label = "Ingredients (use commas)",
                icon = Icons.Default.Description,
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormTextField(
                value = steps,
                onValueChange = viewModel::onStepsChange,
                label = "Instructions (use commas)",
                icon = Icons.Default.Description,
                singleLine = false,
                minLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Media",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                value = videoUrl,
                onValueChange = viewModel::onVideoUrlChange,
                label = "YouTube URL (Optional)",
                icon = Icons.Default.VideoLibrary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.submitRecipe() },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                enabled = uiState !is AddEditUiState.Loading,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState is AddEditUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (recipeId == null) "CREATE RECIPE" else "UPDATE RECIPE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
