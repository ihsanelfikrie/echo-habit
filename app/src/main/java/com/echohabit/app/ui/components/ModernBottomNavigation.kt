package com.echohabit.app.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.echohabit.app.ui.theme.PrimaryGreen

private const val TAG = "ModernBottomNav"

@Composable
fun ModernBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "🎨 ModernBottomNavigation rendered, selectedTab = $selectedTab")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab (0)
            NavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = {
                    Log.d(TAG, "✅ Home NavItem clicked")
                    onTabSelected(0)
                }
            )

            // Stats Tab (1)
            NavItem(
                icon = Icons.Outlined.BarChart,
                label = "Stats",
                isSelected = selectedTab == 1,
                onClick = {
                    Log.d(TAG, "✅ Stats NavItem clicked")
                    onTabSelected(1)
                }
            )

            // CENTER FAB - Upload (Tab 2)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            Log.d(TAG, "✅✅✅ CENTER FAB CLICKED! ✅✅✅")
                            onTabSelected(2)
                        }
                    )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    shape = CircleShape,
                    color = PrimaryGreen,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Upload",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // ✅ BADGES TAB (3) - FIXED!
            NavItem(
                icon = Icons.Outlined.EmojiEvents, // Trophy icon
                label = "Badges",
                isSelected = selectedTab == 3,
                onClick = {
                    Log.d(TAG, "✅ Badges NavItem clicked")
                    onTabSelected(3) // ✅ PROPERLY CALL CALLBACK!
                }
            )

            // Profile Tab (4)
            NavItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                isSelected = selectedTab == 4,
                onClick = {
                    Log.d(TAG, "✅ Profile NavItem clicked")
                    onTabSelected(4)
                }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nav_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else Color(0xFF999999),
        animationSpec = tween(200),
        label = "icon_color"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(
                    bounded = true,
                    radius = 28.dp,
                    color = PrimaryGreen
                ),
                onClick = {
                    Log.d(TAG, "NavItem '$label' clicked")
                    onClick() // ✅ ENSURE CALLBACK IS CALLED!
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                )
            }
        }
    }
}