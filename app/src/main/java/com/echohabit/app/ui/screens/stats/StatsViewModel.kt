package com.echohabit.app.ui.screens.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.AuthRepository
import com.echohabit.app.data.repository.LocalActivityRepository
import com.echohabit.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val authRepository: AuthRepository,
    private val localActivityRepository: LocalActivityRepository // ✅ USE LOCAL!
) : ViewModel() {

    companion object {
        private const val TAG = "StatsViewModel"
    }

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    sealed class StatsUiState {
        object Loading : StatsUiState()
        data class Success(
            val weeklyActivities: List<Activity>,
            val monthlyStats: MonthlyStats,
            val categoryBreakdown: Map<String, Int>
        ) : StatsUiState()
        data class Error(val message: String) : StatsUiState()
    }

    data class MonthlyStats(
        val totalCO2: Double,
        val totalActivities: Int,
        val currentStreak: Int
    )

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading

            try {
                val userId = authRepository.getCurrentUserId()
                if (userId == null) {
                    _uiState.value = StatsUiState.Error("User not logged in")
                    return@launch
                }

                Log.d(TAG, "📊 Loading stats for: $userId")

                // Get recent activities from LOCAL DATABASE
                val activities = when (val result = localActivityRepository.getUserActivities(userId, limit = 30)) {
                    is Result.Success -> result.data
                    is Result.Error -> {
                        Log.e(TAG, "❌ Failed to load activities: ${result.exception.message}")
                        emptyList()
                    }
                    else -> emptyList()
                }

                // Filter to last 7 days for weekly view
                val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                val weeklyActivities = activities.filter {
                    (it.createdAt?.toDate()?.time ?: 0) >= weekAgo
                }

                // Calculate monthly stats (last 30 activities)
                val totalCO2 = activities.sumOf { it.co2SavedKg }
                val totalActivities = activities.size

                // Calculate category breakdown (percentage)
                val categoryCount = activities.groupBy { it.category }
                    .mapValues { it.value.size }

                val total = categoryCount.values.sum()
                val categoryBreakdown = if (total > 0) {
                    categoryCount.mapValues { (it.value * 100) / total }
                } else {
                    emptyMap()
                }

                // Calculate streak (simplified - count consecutive days with activities)
                val streak = calculateStreak(activities)

                val monthlyStats = MonthlyStats(
                    totalCO2 = totalCO2,
                    totalActivities = totalActivities,
                    currentStreak = streak
                )

                Log.d(TAG, "✅ Loaded stats: ${activities.size} activities, ${totalCO2}kg CO2")

                _uiState.value = StatsUiState.Success(
                    weeklyActivities = weeklyActivities,
                    monthlyStats = monthlyStats,
                    categoryBreakdown = categoryBreakdown
                )

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading stats", e)
                _uiState.value = StatsUiState.Error(e.message ?: "Failed to load stats")
            }
        }
    }

    /**
     * Calculate current streak from activities
     */
    private fun calculateStreak(activities: List<Activity>): Int {
        if (activities.isEmpty()) return 0

        // Group by date
        val activitiesByDate = activities
            .groupBy { activity ->
                val timestamp = activity.createdAt?.toDate()?.time ?: 0
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = timestamp
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            .toSortedMap(compareByDescending { it })

        // Check consecutive days
        var streak = 0
        val today = java.util.Calendar.getInstance()
        today.set(java.util.Calendar.HOUR_OF_DAY, 0)
        today.set(java.util.Calendar.MINUTE, 0)
        today.set(java.util.Calendar.SECOND, 0)
        today.set(java.util.Calendar.MILLISECOND, 0)

        var currentDay = today.timeInMillis

        for (i in 0 until 365) { // Max check 1 year
            if (activitiesByDate.containsKey(currentDay)) {
                streak++
                currentDay -= (24 * 60 * 60 * 1000) // Go back 1 day
            } else {
                break
            }
        }

        return streak
    }
}