package com.echohabit.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Activity DAO - Local Database Access
 */
@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: LocalActivity): Long

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY createdAt DESC")
    fun getActivitiesByUser(userId: String): Flow<List<LocalActivity>>

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getActivitiesByUserLimit(userId: String, limit: Int): List<LocalActivity>

    @Query("SELECT * FROM activities WHERE userId = :userId AND createdAt >= :startOfDay AND createdAt <= :endOfDay ORDER BY createdAt DESC")
    suspend fun getTodayActivities(userId: String, startOfDay: Long, endOfDay: Long): List<LocalActivity>

    @Query("SELECT * FROM activities WHERE userId = :userId AND category = :category ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getActivitiesByCategory(userId: String, category: String, limit: Int): List<LocalActivity>

    @Query("SELECT COUNT(*) FROM activities WHERE userId = :userId AND activityType = :activityType")
    suspend fun getActivityCountByType(userId: String, activityType: String): Int

    @Query("SELECT COUNT(*) FROM activities WHERE userId = :userId")
    suspend fun getTotalActivityCount(userId: String): Int

    @Delete
    suspend fun deleteActivity(activity: LocalActivity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: Long)

    @Query("DELETE FROM activities WHERE userId = :userId")
    suspend fun deleteAllActivitiesByUser(userId: String)
}

/**
 * User DAO
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): LocalUser?

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeUser(userId: String): Flow<LocalUser?>

    @Update
    suspend fun updateUser(user: LocalUser)

    @Query("UPDATE users SET totalPoints = totalPoints + :points, totalCO2SavedKg = totalCO2SavedKg + :co2, level = :level WHERE userId = :userId")
    suspend fun updateUserStats(userId: String, points: Int, co2: Double, level: Int)

    @Query("UPDATE users SET currentStreak = :streak, longestStreak = :longestStreak WHERE userId = :userId")
    suspend fun updateStreak(userId: String, streak: Int, longestStreak: Int)

    @Delete
    suspend fun deleteUser(user: LocalUser)
}

/**
 * Stats DAO
 */
@Dao
interface StatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: LocalDailyStats)

    @Query("SELECT * FROM daily_stats WHERE userId = :userId AND date = :date")
    suspend fun getStatsByDate(userId: String, date: String): LocalDailyStats?

    @Query("SELECT * FROM daily_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getStatsRange(userId: String, startDate: String, endDate: String): List<LocalDailyStats>

    @Query("UPDATE daily_stats SET dailyPoints = dailyPoints + :points, dailyCO2Kg = dailyCO2Kg + :co2, activityCount = activityCount + 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStats(id: String, points: Int, co2: Double, updatedAt: Long)
}