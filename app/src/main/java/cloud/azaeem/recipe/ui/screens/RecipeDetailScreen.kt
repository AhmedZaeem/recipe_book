package cloud.azaeem.recipe.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cloud.azaeem.recipe.viewmodel.RecipeDetailUiState
import cloud.azaeem.recipe.viewmodel.RecipeDetailViewModel
import coil.compose.AsyncImage
import java.net.URLDecoder

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: RecipeDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    when (val state = uiState) {
        is RecipeDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is RecipeDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is RecipeDetailUiState.Success -> {
            val recipe = state.recipe
            val isCreator = state.isCreator
            
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                        startY = 0f
                                    )
                                )
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                            }
                            
                            if (isCreator) {
                                Row {
                                    IconButton(
                                        onClick = { onNavigateToEdit(recipe.id) },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.2f))
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { 
                                            viewModel.deleteRecipe(recipeId)
                                            onNavigateBack()
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = recipe.category.uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        recipe.ingredientsList.forEach { ingredient ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = ingredient,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Method",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        recipe.stepsList.forEachIndexed { index, step ->
                            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                                Text(
                                    text = (index + 1).toString().padStart(2, '0'),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = 28.sp
                                    )
                                    if (index < recipe.stepsList.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }

                        if (!recipe.videoUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(32.dp))
                            val ytId = rememberYoutubeId(recipe.videoUrl)
                            val thumb = ytId?.let { "https://img.youtube.com/vi/$it/hqdefault.jpg" }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.videoUrl))
                                        context.startActivity(intent)
                                    },
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (thumb != null) {
                                        AsyncImage(
                                            model = thumb,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color.Black.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.7f))
                                                    )
                                                )
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Watch tutorial",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "YouTube",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberYoutubeId(url: String?): String? {
    if (url.isNullOrBlank()) return null
    val u = url.trim()
    val patterns = listOf(
        Regex("youtu\\.be/([A-Za-z0-9_-]{6,})"),
        Regex("youtube\\.com/watch\\?v=([A-Za-z0-9_-]{6,})"),
        Regex("youtube\\.com/embed/([A-Za-z0-9_-]{6,})")
    )
    for (p in patterns) {
        val m = p.find(u)
        if (m != null) return m.groupValues[1]
    }
    val vParam = Regex("[?&]v=([^&]+)").find(u)?.groupValues?.getOrNull(1)
    return vParam?.let { runCatching { URLDecoder.decode(it, Charsets.UTF_8.name()) }.getOrNull() }
}
