package com.echohabit.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreen.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryGreen,

    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreen.copy(alpha = 0.1f),
    onSecondaryContainer = SecondaryGreen,

    tertiary = AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AccentBlue.copy(alpha = 0.1f),
    onTertiaryContainer = AccentBlue,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreen.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryGreen,

    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreen.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryGreen,

    tertiary = AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AccentBlue.copy(alpha = 0.2f),
    onTertiaryContainer = AccentBlue,

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardBGDark,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,

    outline = BorderPrimary,
    outlineVariant = BorderPrimary.copy(alpha = 0.5f)
)

@Composable
fun EchoHabitTheme(
    darkTheme: Boolean = false, // DEFAULT TO LIGHT THEME!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}