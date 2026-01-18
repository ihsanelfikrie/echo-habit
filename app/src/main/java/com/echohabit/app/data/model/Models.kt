package com.echohabit.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

// User Model
data class User(
    @DocumentId
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalCO2SavedKg: Double = 0.0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val badges: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val lastActiveAt: Timestamp? = null
)

// Activity Model
data class Activity(
    @DocumentId
    val activityId: String = "",
    val userId: String = "",
    val category: String = "",
    val activityType: String = "",
    val photoUrl: String = "",
    val caption: String = "",
    val points: Int = 0,
    val co2SavedKg: Double = 0.0,
    val cardStyle: String = "glassmorphism",
    val cardImageUrl: String = "",
    val sharedTo: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

// Stats Model (Daily Aggregation)
data class DailyStats(
    val userId: String = "",
    val date: String = "", // Format: YYYY-MM-DD
    val dailyPoints: Int = 0,
    val dailyCO2Kg: Double = 0.0,
    val activityCount: Int = 0,
    val breakdown: Map<String, Int> = emptyMap(), // category -> percentage
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

// Badge Model
data class Badge(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val description: String = "",
    val isUnlocked: Boolean = false,
    val unlockedAt: Timestamp? = null
)

// Card Style Enum
enum class CardStyle(val value: String) {
    GLASSMORPHISM("glassmorphism"),
    SPLIT("split"),
    MINIMALIST("minimalist");

    companion object {
        fun fromString(value: String): CardStyle {
            return values().find { it.value == value } ?: GLASSMORPHISM
        }
    }
}

// Category Enum
enum class Category(val value: String, val displayName: String, val emoji: String) {
    MOVE_GREEN("move_green", "Move Green", "🚴"),
    EAT_CLEAN("eat_clean", "Eat Clean", "🥗"),
    CUT_WASTE("cut_waste", "Cut Waste", "♻️"),
    SAVE_ENERGY("save_energy", "Save Energy", "💡");

    companion object {
        fun fromString(value: String): Category {
            return values().find { it.value == value } ?: MOVE_GREEN
        }
    }
}

// Result wrapper for async operations
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}