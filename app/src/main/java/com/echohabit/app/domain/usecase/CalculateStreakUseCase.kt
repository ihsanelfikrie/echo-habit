package com.echohabit.app.domain.usecase

import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.repository.ActivityRepository
import com.echohabit.app.data.model.Result
import com.echohabit.app.util.DateUtils
import com.google.firebase.Timestamp
import java.util.*

class CalculateStreakUseCase(
    private val activityRepository: ActivityRepository
) {

    data class StreakData(
        val currentStreak: Int,
        val hasActivityToday: Boolean,
        val lastActivityDate: String?,
        val shouldUpdateStreak: Boolean
    )

    /**
     * Calculate current streak based on activities
     */
    suspend operator fun invoke(userId: String, currentStreak: Int): Result<StreakData> {
        return try {
            // Get recent activities
            when (val result = activityRepository.getUserActivities(userId, limit = 100)) {
                is Result.Success -> {
                    val activities = result.data
                    val streakData = calculateStreakFromActivities(activities, currentStreak)
                    Result.Success(streakData)
                }
                is Result.Error -> result
                else -> Result.Error(Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Calculate streak from activity list
     */
    private fun calculateStreakFromActivities(
        activities: List<Activity>,
        currentStreak: Int
    ): StreakData {
        if (activities.isEmpty()) {
            return StreakData(
                currentStreak = 0,
                hasActivityToday = false,
                lastActivityDate = null,
                shouldUpdateStreak = currentStreak != 0
            )
        }

        // Group activities by date
        val activitiesByDate = activities
            .groupBy { activity ->
                DateUtils.timestampToDateString(activity.createdAt)
            }
            .toSortedMap(compareByDescending { it })

        val today = DateUtils.getCurrentDateString()
        val yesterday = getYesterdayDateString()

        val hasActivityToday = activitiesByDate.containsKey(today)
        val hasActivityYesterday = activitiesByDate.containsKey(yesterday)

        // Calculate new streak
        val newStreak = when {
            hasActivityToday -> {
                // Already logged today, maintain or increase streak
                calculateConsecutiveDays(activitiesByDate)
            }
            hasActivityYesterday -> {
                // Last activity was yesterday, streak is alive
                currentStreak
            }
            else -> {
                // Streak is broken
                0
            }
        }

        return StreakData(
            currentStreak = newStreak,
            hasActivityToday = hasActivityToday,
            lastActivityDate = activitiesByDate.keys.firstOrNull(),
            shouldUpdateStreak = newStreak != currentStreak
        )
    }

    /**
     * Calculate consecutive days with activities
     */
    private fun calculateConsecutiveDays(activitiesByDate: Map<String, List<Activity>>): Int {
        var streak = 0
        var currentDate = Calendar.getInstance()

        for (i in 0 until 365) { // Max check 1 year
            val dateString = DateUtils.timestampToDateString(Timestamp(currentDate.time))

            if (activitiesByDate.containsKey(dateString)) {
                streak++
                currentDate.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Get yesterday's date string
     */
    private fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return DateUtils.timestampToDateString(Timestamp(calendar.time))
    }
}