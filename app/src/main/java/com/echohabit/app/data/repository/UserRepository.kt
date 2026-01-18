package com.echohabit.app.data.repository

import android.util.Log
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.model.User
import com.echohabit.app.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val TAG = "UserRepository"
    }

    /**
     * Get user by ID
     */
    suspend fun getUser(userId: String): Result<User> {
        return try {
            Log.d(TAG, "Fetching user: $userId")

            val snapshot = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    Log.d(TAG, "User found: ${user.username}")
                    Result.Success(user)
                } else {
                    Log.e(TAG, "Failed to parse user document")
                    Result.Error(Exception("Failed to parse user data"))
                }
            } else {
                Log.e(TAG, "User document does not exist")
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user", e)
            Result.Error(e)
        }
    }

    /**
     * Observe user changes (real-time)
     */
    fun observeUser(userId: String): Flow<Result<User>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing user", error)
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Result.Success(user))
                    } else {
                        trySend(Result.Error(Exception("Failed to parse user")))
                    }
                } else {
                    trySend(Result.Error(Exception("User not found")))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Update user points and CO2
     */
    suspend fun updateUserStats(
        userId: String,
        pointsToAdd: Int,
        co2ToAdd: Double
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating stats for user: $userId, points: +$pointsToAdd, CO2: +$co2ToAdd")

            val userRef = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                if (!snapshot.exists()) {
                    Log.w(TAG, "User document does not exist, skipping stats update")
                    return@runTransaction
                }

                val currentPoints = snapshot.getLong("totalPoints")?.toInt() ?: 0
                val currentCO2 = snapshot.getDouble("totalCO2SavedKg") ?: 0.0

                val newPoints = currentPoints + pointsToAdd
                val newCO2 = currentCO2 + co2ToAdd

                // Calculate new level
                val newLevel = Constants.LEVELS.find {
                    newPoints in it.minPoints..it.maxPoints
                }?.let { Constants.LEVELS.indexOf(it) + 1 } ?: 1

                transaction.update(userRef, mapOf(
                    "totalPoints" to newPoints,
                    "totalCO2SavedKg" to newCO2,
                    "level" to newLevel
                ))

                Log.d(TAG, "Stats updated successfully")
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating stats", e)
            Result.Error(e)
        }
    }

    /**
     * Update user streak
     */
    suspend fun updateStreak(userId: String, newStreak: Int): Result<Unit> {
        return try {
            Log.d(TAG, "Updating streak for user: $userId, new streak: $newStreak")

            val userRef = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                if (!snapshot.exists()) {
                    Log.w(TAG, "User document does not exist, skipping streak update")
                    return@runTransaction
                }

                val longestStreak = snapshot.getLong("longestStreak")?.toInt() ?: 0

                val updates = mutableMapOf<String, Any>(
                    "currentStreak" to newStreak
                )

                // Update longest streak if needed
                if (newStreak > longestStreak) {
                    updates["longestStreak"] = newStreak
                }

                transaction.update(userRef, updates)

                Log.d(TAG, "Streak updated successfully")
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating streak", e)
            Result.Error(e)
        }
    }

    /**
     * Unlock badge
     */
    suspend fun unlockBadge(userId: String, badgeId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Unlocking badge for user: $userId, badge: $badgeId")

            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update("badges", com.google.firebase.firestore.FieldValue.arrayUnion(badgeId))
                .await()

            Log.d(TAG, "Badge unlocked successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking badge", e)
            Result.Error(e)
        }
    }

    /**
     * Update user profile
     */
    suspend fun updateProfile(
        userId: String,
        displayName: String? = null,
        photoUrl: String? = null
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating profile for user: $userId")

            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoUrl"] = it }

            if (updates.isNotEmpty()) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(userId)
                    .update(updates)
                    .await()

                Log.d(TAG, "Profile updated successfully")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile", e)
            Result.Error(e)
        }
    }
}