package com.echohabit.app.ui.screens.splash

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.echohabit.app.R
import com.echohabit.app.ui.theme.PrimaryGreen
import com.echohabit.app.util.Constants
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val navigationState by viewModel.navigationState.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(navigationState) {
        Log.d("SplashScreen", "📌 LaunchedEffect triggered")
        Log.d("SplashScreen", "📊 Current navigationState: $navigationState")

        if (!hasNavigated) {
            when (navigationState) {
                is SplashViewModel.NavigationState.NavigateToHome -> {
                    Log.d("SplashScreen", "✅ Navigation condition met: HOME")
                    hasNavigated = true
                    try {
                        onNavigateToHome()
                        Log.d("SplashScreen", "✅ onNavigateToHome() executed")
                    } catch (e: Exception) {
                        Log.e("SplashScreen", "❌ ERROR executing onNavigateToHome()", e)
                    }
                }
                is SplashViewModel.NavigationState.NavigateToLogin -> {
                    Log.d("SplashScreen", "✅ Navigation condition met: LOGIN")
                    hasNavigated = true
                    try {
                        onNavigateToLogin()
                        Log.d("SplashScreen", "✅ onNavigateToLogin() executed")
                    } catch (e: Exception) {
                        Log.e("SplashScreen", "❌ ERROR executing onNavigateToLogin()", e)
                    }
                }
                is SplashViewModel.NavigationState.Loading -> {
                    Log.d("SplashScreen", "⏳ Still loading...")
                }
            }
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    Log.d("SplashScreen", "🎨 Rendering SplashContent")

    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // ✅ WHITE BACKGROUND!
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ✅ YOUR LOGO IMAGE!
            AsyncImage(
                model = R.drawable.logo, // ✅ FIXED!
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp) // Larger size for better visibility
                    .scale(scale),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name - BOLD!
            Text(
                text = Constants.APP_NAME,
                style = MaterialTheme.typography.displayLarge, // Larger!
                fontWeight = FontWeight.ExtraBold, // Extra Bold!
                color = PrimaryGreen,
                letterSpacing = 1.sp // Better spacing
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline - THIN!
            Text(
                text = "Track Your Eco Impact",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light, // Light weight!
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(120.dp))

            // Developer info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Developed by",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999)
                )
                Text(
                    text = Constants.DEVELOPER_NAME,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = Constants.DEVELOPER_NIM,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}