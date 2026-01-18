package com.echohabit.app.data.repository

import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.Result
import com.echohabit.app.util.Constants
import com.echohabit.app.util.DateUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ActivityRepository(
    private val firestore: FirebaseFirestore
) {

    /**
     * Create new activity
     */
    suspend fun createActivity(activity: Activity): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .add(activity)
                .await()

            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get user activities (paginated)
     */
    suspend fun getUserActivities(
        userId: String,
        limit: Int = 20
    ): Result<List<Activity>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val activities = snapshot.toObjects(Activity::class.java)
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
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()

            val snapshot = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("createdAt", startOfDay)
                .whereLessThanOrEqualTo("createdAt", endOfDay)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val activities = snapshot.toObjects(Activity::class.java)
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
            val snapshot = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("category", category)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val activities = snapshot.toObjects(Activity::class.java)
            Result.Success(activities)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get activity count by type (for badge checking)
     */
    suspend fun getActivityCountByType(
        userId: String,
        activityType: String
    ): Result<Int> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("activityType", activityType)
                .get()
                .await()

            Result.Success(snapshot.size())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get total activity count
     */
    suspend fun getTotalActivityCount(userId: String): Result<Int> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Result.Success(snapshot.size())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update activity card URL after generation
     */
    suspend fun updateActivityCard(
        activityId: String,
        cardImageUrl: String,
        cardStyle: String
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .document(activityId)
                .update(mapOf(
                    "cardImageUrl" to cardImageUrl,
                    "cardStyle" to cardStyle
                ))
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update shared platforms
     */
    suspend fun updateSharedPlatforms(
        activityId: String,
        platform: String
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .document(activityId)
                .update("sharedTo", com.google.firebase.firestore.FieldValue.arrayUnion(platform))
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete activity
     */
    suspend fun deleteActivity(activityId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_ACTIVITIES)
                .document(activityId)
                .delete()
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}