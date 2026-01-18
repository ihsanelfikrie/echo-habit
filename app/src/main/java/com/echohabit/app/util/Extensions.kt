package com.echohabit.app.util

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.NumberFormat
import java.util.Locale

/**
 * Show toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Format double to CO2 string with 1 decimal
 */
fun Double.toCO2String(): String {
    return String.format(Locale.getDefault(), "%.1f", this)
}

/**
 * Format int to points string with thousand separator
 */
fun Int.toPointsString(): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(this)
}

/**
 * Clickable without ripple effect
 */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

/**
 * Safe string truncation
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        "${this.take(maxLength)}..."
    } else {
        this
    }
}

/**
 * Convert string to title case
 */
fun String.toTitleCase(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
}

/**
 * Check if string is valid email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Generate username from email
 */
fun String.toUsername(): String {
    return this.substringBefore("@")
        .replace(".", "_")
        .replace("-", "_")
        .lowercase(Locale.getDefault())
}