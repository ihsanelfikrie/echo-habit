package com.echohabit.app.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.echohabit.app.ui.theme.*
import com.echohabit.app.util.toCO2String
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = koinViewModel() // ✅ USE VIEWMODEL!
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
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
            is StatsViewModel.StatsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            is StatsViewModel.StatsUiState.Success -> {
                StatsContent(
                    monthlyStats = state.monthlyStats,
                    categoryBreakdown = state.categoryBreakdown,
                    modifier = Modifier.padding(padding)
                )
            }
            is StatsViewModel.StatsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = state.message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadStats() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsContent(
    monthlyStats: StatsViewModel.MonthlyStats,
    categoryBreakdown: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        // Overview Card - ✅ REAL DATA!
        item {
            StatsOverviewCard(monthlyStats)
        }

        // Category Breakdown - ✅ REAL DATA!
        item {
            Text(
                text = "CATEGORY BREAKDOWN",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            CategoryBreakdownCard(categoryBreakdown)
        }

        // Weekly Trend (Placeholder for future chart)
        item {
            Text(
                text = "WEEKLY TREND",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyTrendCard()
        }

        // Milestones
        item {
            Text(
                text = "MILESTONES",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            MilestonesCard(monthlyStats)
        }
    }
}

@Composable
private fun StatsOverviewCard(stats: StatsViewModel.MonthlyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "THIS MONTH",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ REAL DATA!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(
                    value = stats.totalCO2.toCO2String(),
                    unit = "kg CO₂",
                    label = "Saved",
                    color = NeoMint
                )
                StatColumn(
                    value = stats.totalActivities.toString(),
                    unit = "activities",
                    label = "Logged",
                    color = ElectricLime
                )
                StatColumn(
                    value = stats.currentStreak.toString(),
                    unit = "days",
                    label = "Streak",
                    color = HotPink
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    unit: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CategoryBreakdownCard(breakdown: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            if (breakdown.isEmpty()) {
                Text(
                    text = "No activities yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // ✅ MODERN ICONS - No Emoji!
                breakdown["move_green"]?.let { percentage ->
                    CategoryBreakdownItem(
                        icon = Icons.Outlined.DirectionsBike,
                        label = "Move Green",
                        percentage = percentage,
                        color = MoveGreen
                    )
                    if (breakdown.size > 1) Spacer(modifier = Modifier.height(12.dp))
                }
                breakdown["eat_clean"]?.let { percentage ->
                    CategoryBreakdownItem(
                        icon = Icons.Outlined.Restaurant,
                        label = "Eat Clean",
                        percentage = percentage,
                        color = EatClean
                    )
                    if (breakdown.size > 2) Spacer(modifier = Modifier.height(12.dp))
                }
                breakdown["cut_waste"]?.let { percentage ->
                    CategoryBreakdownItem(
                        icon = Icons.Outlined.Recycling,
                        label = "Cut Waste",
                        percentage = percentage,
                        color = CutWaste
                    )
                    if (breakdown.size > 3) Spacer(modifier = Modifier.height(12.dp))
                }
                breakdown["save_energy"]?.let { percentage ->
                    CategoryBreakdownItem(
                        icon = Icons.Outlined.Lightbulb,
                        label = "Save Energy",
                        percentage = percentage,
                        color = SaveEnergy
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownItem(
    icon: ImageVector,
    label: String,
    percentage: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ ICON INSTEAD OF EMOJI!
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun WeeklyTrendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ MODERN ICON - No Emoji!
            Icon(
                imageVector = Icons.Outlined.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chart Coming Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Weekly activity trends will be displayed here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MilestonesCard(stats: StatsViewModel.MonthlyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // ✅ MODERN ICONS - No Emoji!
            MilestoneItem(
                icon = Icons.Outlined.Flag,
                title = "First Activity",
                status = if (stats.totalActivities > 0) "Completed ✅" else "Locked",
                color = if (stats.totalActivities > 0) PrimaryGreen else Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            MilestoneItem(
                icon = Icons.Outlined.LocalFireDepartment,
                title = "7 Day Streak",
                status = if (stats.currentStreak >= 7) "Completed ✅" else "In Progress: ${stats.currentStreak}/7",
                color = if (stats.currentStreak >= 7) HotPink else Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            MilestoneItem(
                icon = Icons.Outlined.Eco,
                title = "50 kg CO₂ Saved",
                status = if (stats.totalCO2 >= 50) "Completed ✅" else "In Progress: ${stats.totalCO2.toCO2String()}/50",
                color = if (stats.totalCO2 >= 50) MoveGreen else Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            MilestoneItem(
                icon = Icons.Outlined.EmojiEvents,
                title = "30 Day Streak",
                status = if (stats.currentStreak >= 30) "Completed ✅" else "Locked",
                color = if (stats.currentStreak >= 30) SaveEnergy else Color.Gray
            )
        }
    }
}

@Composable
private fun MilestoneItem(
    icon: ImageVector,
    title: String,
    status: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ ICON INSTEAD OF EMOJI!
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}