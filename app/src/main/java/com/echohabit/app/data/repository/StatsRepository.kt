package com.echohabit.app.data.repository

import com.echohabit.app.data.model.DailyStats
import com.echohabit.app.data.model.Result
import com.echohabit.app.util.Constants
import com.echohabit.app.util.DateUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class StatsRepository(
    private val firestore: FirebaseFirestore
) {

    /**
     * Get daily stats for a specific date
     */
    suspend fun getDailyStats(userId: String, date: String): Result<DailyStats> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_STATS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .limit(1)
                .get()
                .await()

            val stats = snapshot.documents.firstOrNull()?.toObject(DailyStats::class.java)
            if (stats != null) {
                Result.Success(stats)
            } else {
                // Return empty stats
                Result.Success(DailyStats(userId = userId, date = date))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get stats for date range
     */
    suspend fun getStatsRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Result<List<DailyStats>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_STATS)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val statsList = snapshot.toObjects(DailyStats::class.java)
            Result.Success(statsList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update or create daily stats
     */
    suspend fun updateDailyStats(
        userId: String,
        date: String,
        pointsToAdd: Int,
        co2ToAdd: Double,
        category: String
    ): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_STATS)
                .document("${userId}_$date")

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                if (snapshot.exists()) {
                    // Update existing stats
                    val currentPoints = snapshot.getLong("dailyPoints")?.toInt() ?: 0
                    val currentCO2 = snapshot.getDouble("dailyCO2Kg") ?: 0.0
                    val currentCount = snapshot.getLong("activityCount")?.toInt() ?: 0
                    val currentBreakdown = snapshot.get("breakdown") as? Map<String, Int> ?: emptyMap()

                    val newBreakdown = currentBreakdown.toMutableMap()
                    newBreakdown[category] = (newBreakdown[category] ?: 0) + 1

                    transaction.update(docRef, mapOf(
                        "dailyPoints" to (currentPoints + pointsToAdd),
                        "dailyCO2Kg" to (currentCO2 + co2ToAdd),
                        "activityCount" to (currentCount + 1),
                        "breakdown" to newBreakdown,
                        "updatedAt" to Timestamp.now()
                    ))
                } else {
                    // Create new stats
                    val newStats = DailyStats(
                        userId = userId,
                        date = date,
                        dailyPoints = pointsToAdd,
                        dailyCO2Kg = co2ToAdd,
                        activityCount = 1,
                        breakdown = mapOf(category to 1),
                        updatedAt = Timestamp.now()
                    )
                    transaction.set(docRef, newStats)
                }
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get today's stats
     */
    suspend fun getTodayStats(userId: String): Result<DailyStats> {
        val today = DateUtils.getCurrentDateString()
        return getDailyStats(userId, today)
    }

    /**
     * Get weekly stats (last 7 days)
     */
    suspend fun getWeeklyStats(userId: String): Result<List<DailyStats>> {
        return try {
            val today = DateUtils.getCurrentDateString()
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
            val weekAgo = DateUtils.timestampToDateString(Timestamp(calendar.time))

            getStatsRange(userId, weekAgo, today)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}