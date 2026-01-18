package com.echohabit.app.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.echohabit.app.ui.screens.auth.LoginScreen
import com.echohabit.app.ui.screens.badges.BadgesScreen
import com.echohabit.app.ui.screens.home.HomeScreen
import com.echohabit.app.ui.screens.onboarding.OnboardingScreen
import com.echohabit.app.ui.screens.profile.ProfileScreen
import com.echohabit.app.ui.screens.splash.SplashScreen
import com.echohabit.app.ui.screens.stats.StatsScreen
import com.echohabit.app.ui.screens.upload.UploadScreen

private const val TAG = "Navigation"

@Composable
fun EchoHabitNavigation() {
    val navController = rememberNavController()

    Log.d(TAG, "🎯 Navigation created")

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash
        composable("splash") {
            Log.d(TAG, "🏁 Screen: Splash")
            SplashScreen(
                onNavigateToHome = {
                    Log.d(TAG, "→ Navigate to Home")
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    Log.d(TAG, "→ Navigate to Onboarding")
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        composable("onboarding") {
            Log.d(TAG, "📖 Screen: Onboarding")
            OnboardingScreen(
                onFinish = {
                    Log.d(TAG, "→ Onboarding finished, go to Login")
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable("login") {
            Log.d(TAG, "🔐 Screen: Login")
            LoginScreen(
                onLoginSuccess = {
                    Log.d(TAG, "→ Navigate to Home")
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable("home") {
            Log.d(TAG, "🏠 Screen: Home")
            HomeScreen(
                onNavigateToUpload = {
                    Log.d(TAG, "→ Navigate to Upload")
                    navController.navigate("upload")
                },
                onNavigateToProfile = {
                    Log.d(TAG, "→ Navigate to Profile")
                    navController.navigate("profile")
                },
                onNavigateToStats = {
                    Log.d(TAG, "→ Navigate to Stats")
                    navController.navigate("stats")
                },
                onNavigateToBadges = {
                    Log.d(TAG, "→ Navigate to Badges")
                    navController.navigate("badges")
                },
                onLogout = {
                    Log.d(TAG, "→ Logout to Login")
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Upload
        composable("upload") {
            Log.d(TAG, "📸 Screen: Upload")
            UploadScreen(
                onNavigateBack = {
                    Log.d(TAG, "← Back from Upload")
                    navController.popBackStack()
                },
                onUploadSuccess = {
                    Log.d(TAG, "✅ Upload success, back to Home")
                    navController.popBackStack()
                }
            )
        }

        // Profile
        composable("profile") {
            Log.d(TAG, "👤 Screen: Profile")
            ProfileScreen(
                onNavigateBack = {
                    Log.d(TAG, "← Back from Profile")
                    navController.popBackStack()
                },
                onLogout = {
                    Log.d(TAG, "→ Logout to Login")
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Stats
        composable("stats") {
            Log.d(TAG, "📊 Screen: Stats")
            StatsScreen(
                onNavigateBack = {
                    Log.d(TAG, "← Back from Stats")
                    navController.popBackStack()
                }
            )
        }

        // ✅ BADGES SCREEN (NEW!)
        composable("badges") {
            Log.d(TAG, "🏆 Screen: Badges")
            BadgesScreen(
                onNavigateBack = {
                    Log.d(TAG, "← Back from Badges")
                    navController.popBackStack()
                }
            )
        }
    }
}