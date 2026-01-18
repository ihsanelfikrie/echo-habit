package com.echohabit.app.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.PhotoRepository
import com.echohabit.app.util.CardGenerator
import com.echohabit.app.util.Constants

class GenerateCardUseCase(
    private val cardGenerator: CardGenerator
) {

    data class CardRequest(
        val photoUri: Uri,
        val activityType: String,
        val co2SavedKg: Double,
        val streak: Int,
        val username: String,
        val cardStyle: String
    )

    /**
     * Generate card bitmap based on style
     */
    suspend operator fun invoke(request: CardRequest): Result<Bitmap> {
        return try {
            val bitmap = when (request.cardStyle) {
                Constants.CARD_STYLE_GLASSMORPHISM -> {
                    cardGenerator.generateGlassmorphismCard(
                        photoUri = request.photoUri,
                        co2SavedKg = request.co2SavedKg,
                        activityType = request.activityType,
                        streak = request.streak,
                        username = request.username
                    )
                }
                Constants.CARD_STYLE_SPLIT -> {
                    cardGenerator.generateSplitCard(
                        photoUri = request.photoUri,
                        co2SavedKg = request.co2SavedKg,
                        activityType = request.activityType,
                        streak = request.streak,
                        username = request.username
                    )
                }
                Constants.CARD_STYLE_MINIMALIST -> {
                    cardGenerator.generateMinimalistCard(
                        photoUri = request.photoUri,
                        co2SavedKg = request.co2SavedKg,
                        activityType = request.activityType,
                        streak = request.streak
                    )
                }
                else -> {
                    // Default to glassmorphism
                    cardGenerator.generateGlassmorphismCard(
                        photoUri = request.photoUri,
                        co2SavedKg = request.co2SavedKg,
                        activityType = request.activityType,
                        streak = request.streak,
                        username = request.username
                    )
                }
            }

            Result.Success(bitmap)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}