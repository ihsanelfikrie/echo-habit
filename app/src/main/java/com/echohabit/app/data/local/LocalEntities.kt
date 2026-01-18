package com.echohabit.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Activity Entity - Stored in Room Database
 */
@Entity(tableName = "activities")
data class LocalActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val category: String,
    val activityType: String,
    val photoPath: String, // Local file path
    val caption: String,
    val points: Int,
    val co2SavedKg: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

/**
 * Local User Entity
 */
@Entity(tableName = "users")
data class LocalUser(
    @PrimaryKey
    val userId: String,
    val username: String,
    val displayName: String,
    val email: String,
    val photoUrl: String,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalCO2SavedKg: Double = 0.0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)

/**
 * Local Daily Stats
 */
@Entity(tableName = "daily_stats")
data class LocalDailyStats(
    @PrimaryKey
    val id: String, // userId_date format
    val userId: String,
    val date: String, // YYYY-MM-DD
    val dailyPoints: Int = 0,
    val dailyCO2Kg: Double = 0.0,
    val activityCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)