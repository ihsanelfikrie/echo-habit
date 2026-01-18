package com.echohabit.app.domain.usecase

import android.net.Uri
import com.echohabit.app.data.model.Activity
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.ActivityRepository
import com.echohabit.app.data.repository.PhotoRepository
import com.google.firebase.Timestamp

class UploadActivityUseCase(
    private val activityRepository: ActivityRepository,
    private val photoRepository: PhotoRepository
) {

    data class UploadRequest(
        val userId: String,
        val category: String,
        val activityType: String,
        val photoUri: Uri,
        val caption: String,
        val points: Int,
        val co2SavedKg: Double
    )

    /**
     * Upload activity with photo
     */
    suspend operator fun invoke(request: UploadRequest): Result<String> {
        return try {
            // 1. Upload photo first
            val photoResult = photoRepository.uploadActivityPhoto(
                userId = request.userId,
                imageUri = request.photoUri
            )

            when (photoResult) {
                is Result.Success -> {
                    // 2. Create activity document
                    val activity = Activity(
                        userId = request.userId,
                        category = request.category,
                        activityType = request.activityType,
                        photoUrl = photoResult.data,
                        caption = request.caption,
                        points = request.points,
                        co2SavedKg = request.co2SavedKg,
                        cardStyle = "glassmorphism",
                        cardImageUrl = "",
                        sharedTo = emptyList(),
                        createdAt = Timestamp.now()
                    )

                    // 3. Save to Firestore
                    activityRepository.createActivity(activity)
                }
                is Result.Error -> photoResult
                else -> Result.Error(Exception("Unknown error during photo upload"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}