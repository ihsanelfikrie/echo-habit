package com.echohabit.app.data.repository

import android.content.Context
import android.net.Uri
import com.echohabit.app.data.local.ActivityDao
import com.echohabit.app.data.local.LocalActivity
import com.echohabit.app.data.local.StatsDao
import com.echohabit.app.data.local.UserDao
import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.Result
import com.echohabit.app.util.DateUtils
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Local Activity Repository - NO FIREBASE REQUIRED!
 */
class LocalActivityRepository(
    private val context: Context,
    private val activityDao: ActivityDao,
    private val userDao: UserDao,
    private val statsDao: StatsDao
) {

    /**
     * Save photo to internal storage
     */
    private suspend fun savePhotoLocally(uri: Uri, userId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val photoDir = File(context.filesDir, "photos/$userId")
                if (!photoDir.exists()) {
                    photoDir.mkdirs()
                }

                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                val photoFile = File(photoDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(photoFile).use { output ->
                        input.copyTo(output)
                    }
                }

                photoFile.absolutePath
            } catch (e: Exception) {
                throw Exception("Failed to save photo: ${e.message}")
            }
        }
    }

    /**
     * Create new activity - LOCAL STORAGE
     */
    suspend fun createActivity(
        userId: String,
        category: String,
        activityType: String,
        photoUri: Uri,
        caption: String,
        points: Int,
        co2SavedKg: Double
    ): Result<Long> {
        return try {
            // Save photo locally
            val photoPath = savePhotoLocally(photoUri, userId)

            // Create local activity
            val localActivity = LocalActivity(
                userId = userId,
                category = category,
                activityType = activityType,
                photoPath = photoPath,
                caption = caption,
                points = points,
                co2SavedKg = co2SavedKg,
                createdAt = System.currentTimeMillis(),
                isSynced = false
            )

            // Insert to database
            val activityId = activityDao.insertActivity(localActivity)

            // Update user stats
            val user = userDao.getUserById(userId)
            if (user != null) {
                val newPoints = user.totalPoints + points
                val newCO2 = user.totalCO2SavedKg + co2SavedKg

                // Calculate new level
                val newLevel = calculateLevel(newPoints)

                userDao.updateUserStats(userId, points, co2SavedKg, newLevel)
            }

            // Update daily stats
            val today = DateUtils.getCurrentDateString()
            val statsId = "${userId}_$today"
            val existingStats = statsDao.getStatsByDate(userId, today)

            if (existingStats != null) {
                statsDao.updateStats(
                    id = statsId,
                    points = points,
                    co2 = co2SavedKg,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                statsDao.insertStats(
                    com.echohabit.app.data.local.LocalDailyStats(
                        id = statsId,
                        userId = userId,
                        date = today,
                        dailyPoints = points,
                        dailyCO2Kg = co2SavedKg,
                        activityCount = 1,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            Result.Success(activityId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get user activities
     */
    suspend fun getUserActivities(userId: String, limit: Int = 20): Result<List<Activity>> {
        return try {
            val localActivities = activityDao.getActivitiesByUserLimit(userId, limit)
            val activities = localActivities.map { it.toActivity() }
            Result.Success(activities)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get today's activities
     */
    suspend fun getTodayActivities(userId: String): Result<List<Activity>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            val localActivities = activityDao.getTodayActivities(userId, startOfDay, endOfDay)
            val activities = localActivities.map { it.toActivity() }
            Result.Success(activities)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get activities by category
     */
    suspend fun getActivitiesByCategory(
        userId: String,
        category: String,
        limit: Int = 10
    ): Result<List<Activity>> {
        return try {
            val localActivities = activityDao.getActivitiesByCategory(userId, category, limit)
            val activities = localActivities.map { it.toActivity() }
            Result.Success(activities)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get activity count by type
     */
    suspend fun getActivityCountByType(userId: String, activityType: String): Result<Int> {
        return try {
            val count = activityDao.getActivityCountByType(userId, activityType)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get total activity count
     */
    suspend fun getTotalActivityCount(userId: String): Result<Int> {
        return try {
            val count = activityDao.getTotalActivityCount(userId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete activity
     */
    suspend fun deleteActivity(activityId: Long): Result<Unit> {
        return try {
            activityDao.deleteActivityById(activityId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Convert LocalActivity to Activity (Firebase model)
     */
    private fun LocalActivity.toActivity(): Activity {
        return Activity(
            activityId = this.id.toString(),
            userId = this.userId,
            category = this.category,
            activityType = this.activityType,
            photoUrl = "file://${this.photoPath}", // Use file:// URI for local photos
            caption = this.caption,
            points = this.points,
            co2SavedKg = this.co2SavedKg,
            cardStyle = "glassmorphism",
            cardImageUrl = "",
            sharedTo = emptyList(),
            createdAt = Timestamp(Date(this.createdAt))
        )
    }

    /**
     * Calculate level from points
     */
    private fun calculateLevel(points: Int): Int {
        return when {
            points < 100 -> 1
            points < 500 -> 2
            points < 1000 -> 3
            points < 2500 -> 4
            else -> 5
        }
    }
}