package com.echohabit.app.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.echohabit.app.data.model.User
import com.echohabit.app.ui.theme.*
import com.echohabit.app.util.CO2Calculator
import com.echohabit.app.util.toCO2String
import com.echohabit.app.util.toPointsString
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut()
                        onLogout()
                    }
                ) {
                    Text("Logout", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Outlined.Logout, "Logout", tint = ErrorRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        when (val state = uiState) {
            is ProfileViewModel.ProfileUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is ProfileViewModel.ProfileUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    badges = state.badges,
                    modifier = Modifier.padding(padding)
                )
            }
            is ProfileViewModel.ProfileUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.loadProfile() },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    badges: List<ProfileViewModel.BadgeDisplay>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { ProfileHeader(user) }
        item { ImpactSummaryCard(user) }
        item { BadgesSection(badges) }
        item { SettingsSection() }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            AsyncImage(
                model = user.photoUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.displayName}&background=238636&color=fff" },
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "@${user.username}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val levelData = CO2Calculator().getLevel(user.totalPoints)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PrimaryGreen.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = levelData.emoji, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Level ${user.level} • ${levelData.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun ImpactSummaryCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "LIFETIME IMPACT",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(16.dp))

            StatRow(
                icon = Icons.Outlined.Eco,
                label = "Carbon Saved",
                value = "${user.totalCO2SavedKg.toCO2String()} kg CO₂",
                color = MoveGreen
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            StatRow(
                icon = Icons.Outlined.Park,
                label = "Trees Equivalent",
                value = "${CO2Calculator().co2ToTrees(user.totalCO2SavedKg)} trees",
                color = EatClean
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            StatRow(
                icon = Icons.Outlined.Stars,
                label = "Total Points",
                value = user.totalPoints.toPointsString(),
                color = SaveEnergy
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CompactStat(
                    icon = Icons.Outlined.LocalFireDepartment,
                    label = "Current",
                    value = "${user.currentStreak}",
                    color = HotPink
                )
                CompactStat(
                    icon = Icons.Outlined.EmojiEvents,
                    label = "Best",
                    value = "${user.longestStreak}",
                    color = DeepPurple
                )
            }
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp).size(20.dp),
                tint = color
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun CompactStat(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun BadgesSection(badges: List<ProfileViewModel.BadgeDisplay>) {
    Column {
        Text(
            text = "ACHIEVEMENTS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(badges.take(6)) { badge ->
                BadgeCard(badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: ProfileViewModel.BadgeDisplay) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) PrimaryGreen.copy(alpha = 0.15f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = badge.emoji,
                style = MaterialTheme.typography.displaySmall,
                color = if (badge.isUnlocked) Color.Unspecified else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) PrimaryGreen else Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun SettingsSection() {
    Column {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.height(12.dp))

        SettingItem(Icons.Outlined.Notifications, "Notifications") {}
        Spacer(modifier = Modifier.height(8.dp))
        SettingItem(Icons.Outlined.Share, "Share App") {}
        Spacer(modifier = Modifier.height(8.dp))
        SettingItem(Icons.Outlined.Person, "Edit Profile") {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreen
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryGreen)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
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
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Retry")
        }
    }
}