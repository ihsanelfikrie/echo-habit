package com.echohabit.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.model.User
import com.echohabit.app.data.repository.AuthRepository
import com.echohabit.app.data.repository.LocalActivityRepository
import com.echohabit.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val localActivityRepository: LocalActivityRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    sealed class HomeUiState {
        object Loading : HomeUiState()
        data class Success(
            val user: User,
            val todayActivity: Activity?,
            val recentActivities: List<Activity>,
            val weeklyStats: WeeklyStats
        ) : HomeUiState()
        data class Error(val message: String) : HomeUiState()
    }

    data class WeeklyStats(
        val totalCO2: Double,
        val totalPoints: Int,
        val activityCount: Int,
        val breakdown: Map<String, Int>
    )

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val userId = authRepository.getCurrentUserId()

                if (userId == null) {
                    Log.e(TAG, "❌ No userId")
                    _uiState.value = HomeUiState.Error("Not logged in")
                    return@launch
                }

                Log.d(TAG, "📊 Loading home data for: $userId")

                // Get user
                val user = getUserOrCreate(userId)

                // Load activities from LOCAL DATABASE
                val todayActivities = loadActivitiesSafe {
                    localActivityRepository.getTodayActivities(userId)
                }

                val recentActivities = loadActivitiesSafe {
                    localActivityRepository.getUserActivities(userId, limit = 10)
                }

                // Calculate stats
                val weeklyStats = calculateWeeklyStats(recentActivities)

                Log.d(TAG, "✅ Loaded ${recentActivities.size} activities")
                _uiState.value = HomeUiState.Success(
                    user = user,
                    todayActivity = todayActivities.firstOrNull(),
                    recentActivities = recentActivities,
                    weeklyStats = weeklyStats
                )

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading data", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * ✅ DELETE ACTIVITY - NEW FUNCTION!
     */
    fun deleteActivity(activityId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🗑️ Deleting activity: $activityId")

                when (val result = localActivityRepository.deleteActivity(activityId)) {
                    is Result.Success -> {
                        Log.d(TAG, "✅ Activity deleted successfully")
                        // Reload data after delete
                        loadHomeData()
                    }
                    is Result.Error -> {
                        Log.e(TAG, "❌ Failed to delete: ${result.exception.message}")
                        // Still reload to refresh UI
                        loadHomeData()
                    }
                    else -> {
                        loadHomeData()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Delete error", e)
                loadHomeData()
            }
        }
    }

    private suspend fun getUserOrCreate(userId: String): User {
        return try {
            when (val result = userRepository.getUser(userId)) {
                is Result.Success -> result.data
                else -> createDefaultUser(userId)
            }
        } catch (e: Exception) {
            createDefaultUser(userId)
        }
    }

    private fun createDefaultUser(userId: String): User {
        val firebaseUser = authRepository.getCurrentFirebaseUser()
        return User(
            userId = userId,
            username = firebaseUser?.email?.substringBefore("@") ?: "user",
            displayName = firebaseUser?.displayName ?: "Echo User",
            email = firebaseUser?.email ?: "",
            photoUrl = firebaseUser?.photoUrl?.toString() ?: "",
            level = 1,
            totalPoints = 0,
            totalCO2SavedKg = 0.0,
            currentStreak = 0,
            longestStreak = 0,
            badges = emptyList()
        )
    }

    private suspend fun loadActivitiesSafe(
        loader: suspend () -> Result<List<Activity>>
    ): List<Activity> {
        return try {
            when (val result = loader()) {
                is Result.Success -> result.data
                else -> emptyList()
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Failed to load activities", e)
            emptyList()
        }
    }

    private fun calculateWeeklyStats(activities: List<Activity>): WeeklyStats {
        return try {
            val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            val weeklyActivities = activities.filter {
                (it.createdAt?.toDate()?.time ?: 0) >= weekAgo
            }

            val totalCO2 = weeklyActivities.sumOf { it.co2SavedKg }
            val totalPoints = weeklyActivities.sumOf { it.points }

            val categoryCount = weeklyActivities.groupBy { it.category }
                .mapValues { it.value.size }

            val total = categoryCount.values.sum()
            val breakdown = if (total > 0) {
                categoryCount.mapValues { (it.value * 100) / total }
            } else {
                emptyMap()
            }

            WeeklyStats(
                totalCO2 = totalCO2,
                totalPoints = totalPoints,
                activityCount = weeklyActivities.size,
                breakdown = breakdown
            )
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Error calculating stats", e)
            WeeklyStats(0.0, 0, 0, emptyMap())
        }
    }
}