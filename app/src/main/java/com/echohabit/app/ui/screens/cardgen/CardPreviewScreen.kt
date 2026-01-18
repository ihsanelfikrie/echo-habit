package com.echohabit.app.ui.screens.cardgen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.echohabit.app.ui.theme.*
import com.echohabit.app.util.Constants
import com.echohabit.app.util.showToast
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPreviewScreen(
    photoUri: Uri,
    activityType: String,
    co2SavedKg: Double,
    streak: Int,
    username: String,
    onNavigateBack: () -> Unit,
    viewModel: CardViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()

    // Generate card on first launch
    LaunchedEffect(Unit) {
        viewModel.generateCard(
            photoUri = photoUri,
            activityType = activityType,
            co2SavedKg = co2SavedKg,
            streak = streak,
            username = username
        )
    }

    // Handle share success
    LaunchedEffect(uiState) {
        if (uiState is CardViewModel.CardUiState.ShareSuccess) {
            context.showToast("Shared successfully! 🚀")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Impact Card") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is CardViewModel.CardUiState.Generating -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is CardViewModel.CardUiState.Success -> {
                CardPreviewContent(
                    bitmap = state.bitmap,
                    selectedStyle = selectedStyle,
                    onStyleChange = { newStyle ->
                        viewModel.changeStyle(
                            newStyle = newStyle,
                            photoUri = photoUri,
                            activityType = activityType,
                            co2SavedKg = co2SavedKg,
                            streak = streak,
                            username = username
                        )
                    },
                    onShare = { platform ->
                        viewModel.shareCard(platform)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            is CardViewModel.CardUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = {
                        viewModel.generateCard(
                            photoUri, activityType, co2SavedKg, streak, username
                        )
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            is CardViewModel.CardUiState.Sharing -> {
                LoadingContent(
                    modifier = Modifier.padding(padding),
                    message = "Sharing..."
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun CardPreviewContent(
    bitmap: Bitmap,
    selectedStyle: String,
    onStyleChange: (String) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Card Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated Card",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Style Selector
        StyleSelector(
            selectedStyle = selectedStyle,
            onStyleSelected = onStyleChange
        )

        // Share Buttons
        ShareButtons(onShare = onShare)
    }
}

@Composable
private fun StyleSelector(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "STYLE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        val styles = listOf(
            StyleOption(Constants.CARD_STYLE_GLASSMORPHISM, "Glassmorphism", "✨"),
            StyleOption(Constants.CARD_STYLE_SPLIT, "Split Layout", "📐"),
            StyleOption(Constants.CARD_STYLE_MINIMALIST, "Minimalist", "🎯")
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(styles) { style ->
                StyleOptionCard(
                    style = style,
                    isSelected = selectedStyle == style.value,
                    onClick = { onStyleSelected(style.value) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleOptionCard(
    style: StyleOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ElectricLime.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ElectricLime) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = style.emoji,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = style.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ShareButtons(onShare: (String) -> Unit) {
    Column {
        Text(
            text = "SHARE TO",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Main share platforms
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShareButton(
                label = "IG Story",
                icon = "📷",
                color = HotPink,
                onClick = { onShare("instagram") },
                modifier = Modifier.weight(1f)
            )
            ShareButton(
                label = "TikTok",
                icon = "🎵",
                color = Color(0xFF000000),
                onClick = { onShare("tiktok") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShareButton(
                label = "WhatsApp",
                icon = "💬",
                color = Color(0xFF25D366),
                onClick = { onShare("whatsapp") },
                modifier = Modifier.weight(1f)
            )
            ShareButton(
                label = "Save",
                icon = "💾",
                color = DeepPurple,
                onClick = { onShare("save") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generic share button
        Button(
            onClick = { onShare("generic") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share to other apps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShareButton(
    label: String,
    icon: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
    message: String = "Generating your card..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = ElectricLime)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricLime,
                contentColor = Color.Black
            )
        ) {
            Text("Retry")
        }
    }
}

private data class StyleOption(
    val value: String,
    val label: String,
    val emoji: String
)