package com.echohabit.app.ui.screens.badges

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.echohabit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Achievements",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Card
            BadgesHeaderCard(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Badges Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(getBadgesList()) { badge ->
                    AnimatedBadgeCard(badge)
                }
            }
        }
    }
}

@Composable
private fun BadgesHeaderCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "3 / 12", // ✅ Updated total!
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Badges Unlocked",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep going! 9 more to unlock 🚀",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedBadgeCard(badge: BadgeItem) {
    val scale = remember { Animatable(0.8f) }
    var showShareDialog by remember { mutableStateOf(false) }

    LaunchedEffect(badge.isUnlocked) {
        if (badge.isUnlocked) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    // ✅ SHARE DIALOG!
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            icon = {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = badge.color
                )
            },
            title = { Text("Share Badge") },
            text = {
                Column {
                    Text("Share your \"${badge.name}\" achievement!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = badge.progress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Implement actual share functionality
                        // Can integrate with ShareHelper.kt later
                        showShareDialog = false
                    }
                ) {
                    Icon(Icons.Outlined.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Card(
        onClick = { if (badge.isUnlocked) showShareDialog = true }, // ✅ CLICK TO SHARE!
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(if (badge.isUnlocked) scale.value else 1f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) {
                Color.White
            } else {
                Color(0xFFF5F5F5)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (badge.isUnlocked) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background gradient (unlocked only)
            if (badge.isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    badge.color.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Surface(
                    shape = CircleShape,
                    color = if (badge.isUnlocked) {
                        badge.color.copy(alpha = 0.15f)
                    } else {
                        Color(0xFFE0E0E0)
                    }
                ) {
                    Icon(
                        imageVector = badge.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp),
                        tint = if (badge.isUnlocked) badge.color else Color(0xFF999999)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (badge.isUnlocked) FontWeight.Bold else FontWeight.Normal,
                    color = if (badge.isUnlocked) Color(0xFF1A1A1A) else Color(0xFF999999),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress
                Text(
                    text = badge.progress,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (badge.isUnlocked) badge.color else Color(0xFFCCCCCC),
                    textAlign = TextAlign.Center
                )
            }

            // Checkmark (unlocked only)
            if (badge.isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Unlocked",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp),
                    tint = PrimaryGreen
                )
            }

            // ✅ SHARE ICON (unlocked badges only)
            if (badge.isUnlocked) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp),
                        tint = PrimaryGreen
                    )
                }
            }
        }
    }
}

// Badge Data Model
private data class BadgeItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isUnlocked: Boolean,
    val progress: String
)

// ✅ MORE BADGE OPTIONS (12 total - showing future achievements!)
private fun getBadgesList(): List<BadgeItem> {
    return listOf(
        // ✅ UNLOCKED BADGES (3)
        BadgeItem(
            "fire_starter",
            "Fire Starter",
            Icons.Outlined.LocalFireDepartment,
            HotPink,
            isUnlocked = true,
            progress = "7 Days ✓"
        ),
        BadgeItem(
            "pedal_power",
            "Pedal Power",
            Icons.Outlined.DirectionsBike,
            MoveGreen,
            isUnlocked = true,
            progress = "10/10 Rides"
        ),
        BadgeItem(
            "plant_pioneer",
            "Plant Pioneer",
            Icons.Outlined.Restaurant,
            EatClean,
            isUnlocked = true,
            progress = "20/20 Meals"
        ),

        // ✅ IN PROGRESS BADGES (5)
        BadgeItem(
            "waste_warrior",
            "Waste Warrior",
            Icons.Outlined.Recycling,
            CutWaste,
            isUnlocked = false,
            progress = "25/50 Actions"
        ),
        BadgeItem(
            "energy_saver",
            "Energy Saver",
            Icons.Outlined.Lightbulb,
            SaveEnergy,
            isUnlocked = false,
            progress = "18/30 Actions"
        ),
        BadgeItem(
            "planet_hero",
            "Planet Hero",
            Icons.Outlined.Public,
            PrimaryGreen,
            isUnlocked = false,
            progress = "68/100 Activities"
        ),
        BadgeItem(
            "consistency_king",
            "Consistency",
            Icons.Outlined.EmojiEvents,
            DeepPurple,
            isUnlocked = false,
            progress = "12/30 Days"
        ),
        BadgeItem(
            "eco_legend",
            "Eco Legend",
            Icons.Outlined.Stars,
            Color(0xFFFFB84D),
            isUnlocked = false,
            progress = "0/365 Days"
        ),

        // ✅ FUTURE BADGES (4 - Coming Soon)
        BadgeItem(
            "green_commuter",
            "Green Commuter",
            Icons.Outlined.Commute,
            Color(0xFF00D9A3),
            isUnlocked = false,
            progress = "Coming Soon"
        ),
        BadgeItem(
            "zero_waste_master",
            "Zero Waste",
            Icons.Outlined.DeleteOutline,
            Color(0xFFDB6D28),
            isUnlocked = false,
            progress = "Coming Soon"
        ),
        BadgeItem(
            "carbon_neutral",
            "Carbon Neutral",
            Icons.Outlined.CloudOff,
            Color(0xFF6B4FFF),
            isUnlocked = false,
            progress = "Coming Soon"
        ),
        BadgeItem(
            "community_leader",
            "Community Leader",
            Icons.Outlined.Group,
            Color(0xFFFF006E),
            isUnlocked = false,
            progress = "Coming Soon"
        )
    )
}