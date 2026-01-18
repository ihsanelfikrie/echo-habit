package com.echohabit.app.domain.usecase

import android.graphics.Bitmap
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.ActivityRepository
import com.echohabit.app.data.repository.PhotoRepository
import com.echohabit.app.util.ShareHelper

class ShareCardUseCase(
    private val shareHelper: ShareHelper
) {

    /**
     * Share card to platform
     */
    suspend operator fun invoke(
        bitmap: Bitmap,
        platform: String
    ): Result<Boolean> {
        return try {
            val success = when (platform) {
                "instagram" -> shareHelper.shareToInstagramStory(bitmap)
                "tiktok" -> shareHelper.shareToTikTok(bitmap)
                "whatsapp" -> shareHelper.shareToWhatsApp(bitmap)
                "generic" -> shareHelper.shareGeneric(bitmap)
                "save" -> shareHelper.saveToGallery(bitmap)
                else -> false
            }

            if (success) {
                Result.Success(true)
            } else {
                Result.Error(Exception("Failed to share"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}