package com.echohabit.app.ui.screens.home

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.User
import com.echohabit.app.ui.theme.*
import com.echohabit.app.util.CO2Calculator
import com.echohabit.app.util.DateUtils
import com.echohabit.app.util.toCO2String
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    onNavigateToUpload: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        Log.d(TAG, "🔄 Screen visible, reloading data...")
        viewModel.loadHomeData()
        delay(100)
        isVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)) // ✅ Light gray background
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                when (val state = uiState) {
                    is HomeViewModel.HomeUiState.Loading -> {
                        LoadingContent()
                    }
                    is HomeViewModel.HomeUiState.Success -> {
                        ModernHomeContent(
                            user = state.user,
                            todayActivity = state.todayActivity,
                            recentActivities = state.recentActivities,
                            weeklyStats = state.weeklyStats,
                            onDeleteActivity = { activityId ->
                                viewModel.deleteActivity(activityId)
                            },
                            onShareActivity = { activity ->
                                Log.d(TAG, "📤 Share activity: ${activity.activityId}")
                            }
                        )
                    }
                    is HomeViewModel.HomeUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.loadHomeData() }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(999f)
        ) {
            ModernBottomBar(
                onHomeClick = { },
                onStatsClick = onNavigateToStats,
                onUploadClick = onNavigateToUpload,
                onBadgesClick = onNavigateToBadges,
                onProfileClick = onNavigateToProfile,
                selectedTab = 0
            )
        }
    }
}

@Composable
private fun ModernHomeContent(
    user: User,
    todayActivity: Activity?,
    recentActivities: List<Activity>,
    weeklyStats: HomeViewModel.WeeklyStats,
    onDeleteActivity: (Long) -> Unit,
    onShareActivity: (Activity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), // ✅ AUTO STATUS BAR PADDING!
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ✅ 1. HELLO USERNAME + AVATAR (with small streak/level)
        item {
            ModernHeader(user = user)
        }

        // ✅ 2. CO2 SAVED & POINTS ROW (Direct after header!)
        item {
            CO2PointsRow(user = user)
        }

        // ✅ 4. CUSTOM BANNER (ROUNDED)
        item {
            CustomBanner()
        }

        // ✅ 5. WEEKLY STATS (KG/WEEK + ACTIVITIES)
        item {
            WeeklyStatsRow(weeklyStats = weeklyStats)
        }

        // ✅ 6. TODAY'S ACTIVITY
        item {
            SectionHeader(title = "TODAY'S ACTIVITY")
            Spacer(modifier = Modifier.height(8.dp))

            if (todayActivity != null) {
                TodayActivityCard(
                    activity = todayActivity,
                    onDelete = {
                        val id = todayActivity.activityId.toLongOrNull()
                        if (id != null) onDeleteActivity(id)
                    },
                    onShare = { onShareActivity(todayActivity) }
                )
            } else {
                EmptyTodayCard()
            }
        }

        // ✅ 7. RECENT ACTIVITIES
        item {
            SectionHeader(title = "RECENT ACTIVITIES")
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (recentActivities.isEmpty()) {
            item {
                EmptyActivitiesCard()
            }
        } else {
            items(recentActivities.take(5)) { activity ->
                CompactActivityItem(
                    activity = activity,
                    onShare = { onShareActivity(activity) }
                )
            }
        }

        // ✅ 8. ECO TIPS SLIDER (HORIZONTAL SCROLL)
        item {
            SectionHeader(title = "ECO TIPS")
            Spacer(modifier = Modifier.height(8.dp))
            EcoTipsSlider()
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ✅ 1. MODERN HEADER (HELLO FIRSTNAME + SMALL STREAK/LEVEL)
@Composable
private fun ModernHeader(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            // ✅ HELLO FIRSTNAME!
            Text(
                text = "Hello, ${user.displayName.split(" ").firstOrNull() ?: "User"}! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // ✅ SMALL STREAK & LEVEL ROW
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // STREAK
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = HotPink
                    )
                    Text(
                        text = "${user.currentStreak} Day Streak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }

                // SEPARATOR
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC)
                )

                // LEVEL
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SaveEnergy
                    )
                    Text(
                        text = "Level ${user.level}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
        }

        // AVATAR
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = PrimaryGreen.copy(alpha = 0.1f),
            tonalElevation = 2.dp
        ) {
            AsyncImage(
                model = user.photoUrl.ifEmpty {
                    "https://ui-avatars.com/api/?name=${user.displayName}&background=238636&color=fff"
                },
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ✅ 2. STREAK & LEVEL ROW
@Composable
private fun StreakLevelRow(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // STREAK CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = HotPink.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp),
                        tint = HotPink
                    )
                }
                Column {
                    Text(
                        text = "${user.currentStreak}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Day Streak",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }

        // LEVEL CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SaveEnergy.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp),
                        tint = SaveEnergy
                    )
                }
                Column {
                    Text(
                        text = "Level ${user.level}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = CO2Calculator().getLevel(user.totalPoints).name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

// ✅ 3. CO2 SAVED & POINTS ROW
@Composable
private fun CO2PointsRow(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // CO2 SAVED CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MoveGreen.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Eco,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp),
                        tint = MoveGreen
                    )
                }
                Column {
                    Text(
                        text = "${user.totalCO2SavedKg.toCO2String()} kg",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "CO₂ Saved",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }

        // POINTS CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = DeepPurple.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Stars,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp),
                        tint = DeepPurple
                    )
                }
                Column {
                    Text(
                        text = "${user.totalPoints}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Points",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

// ✅ 4. CUSTOM BANNER (ROUNDED CORNERS)
@Composable
private fun CustomBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp), // ✅ ROUNDED!
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // ✅ YOUR CUSTOM BANNER IMAGE!
            AsyncImage(
                model = com.echohabit.app.R.drawable.home_banner,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )

            // GREEN GRADIENT OVERLAY
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryGreen.copy(alpha = 0.85f),
                                SecondaryGreen.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Track Your Impact",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Every action counts for our planet 🌍",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ✅ 5. WEEKLY STATS (NO ROTATION ANIMATION!)
@Composable
private fun WeeklyStatsRow(weeklyStats: HomeViewModel.WeeklyStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // KG/WEEK CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShowChart,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MoveGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weeklyStats.totalCO2.toCO2String()} kg",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }

        // ACTIVITIES CARD
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = SaveEnergy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weeklyStats.activityCount}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Activities",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// ✅ 6. TODAY'S ACTIVITY CARD
@Composable
private fun TodayActivityCard(
    activity: Activity,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val co2Calculator = CO2Calculator()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Activity?") },
            text = { Text("Are you sure you want to delete this activity?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = activity.photoUrl,
                contentDescription = "Activity",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryGreen
                ) {
                    Text(
                        text = "+${activity.co2SavedKg.toCO2String()} kg CO₂",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = co2Calculator.getActivityDisplayName(activity.activityType),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ✅ 7. COMPACT ACTIVITY ITEM
@Composable
private fun CompactActivityItem(
    activity: Activity,
    onShare: () -> Unit
) {
    val co2Calculator = CO2Calculator()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = activity.photoUrl,
                contentDescription = "Activity",
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = co2Calculator.getActivityDisplayName(activity.activityType),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = DateUtils.getRelativeTimeString(activity.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryGreen.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "+${activity.co2SavedKg.toCO2String()}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ✅ 8. ECO TIPS SLIDER (LIKE SALES REVENUE CARDS)
@Composable
private fun EcoTipsSlider() {
    val tips = remember {
        listOf(
            EcoTip("Reduce Plastic", "Use reusable bags and bottles", Color(0xFF7FD957)),
            EcoTip("Save Energy", "Turn off lights when not in use", Color(0xFFFFE14D)),
            EcoTip("Go Green", "Choose public transport or bike", Color(0xFF00D9A3)),
            EcoTip("Recycle More", "Sort your waste properly", Color(0xFFFFB84D))
        )
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tips) { tip ->
            EcoTipCard(tip = tip)
        }
    }
}

@Composable
private fun EcoTipCard(tip: EcoTip) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tip.color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = tip.color.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp),
                    tint = tip.color
                )
            }

            Column {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    maxLines = 2
                )
            }
        }
    }
}

// HELPER COMPONENTS
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF666666),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun EmptyTodayCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF999999)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No activity today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Tap + to log your eco action!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun EmptyActivitiesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Start your eco journey! 🌱",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun ModernBottomBar(
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onUploadClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onProfileClick: () -> Unit,
    selectedTab: Int
) {
    com.echohabit.app.ui.components.ModernBottomNavigation(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            when (tab) {
                0 -> onHomeClick()
                1 -> onStatsClick()
                2 -> onUploadClick()
                3 -> onBadgesClick()
                4 -> onProfileClick()
            }
        }
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryGreen)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ErrorRed
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Oops!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF666666))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Retry")
        }
    }
}

// ✅ DATA CLASS FOR ECO TIPS
private data class EcoTip(
    val title: String,
    val description: String,
    val color: Color
)