package com.echohabit.app.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardGenerator(private val context: Context) {

    private val imageLoader = ImageLoader(context)

    /**
     * Generate card with glassmorphism style
     */
    suspend fun generateGlassmorphismCard(
        photoUri: Uri,
        co2SavedKg: Double,
        activityType: String,
        streak: Int,
        username: String
    ): Bitmap = withContext(Dispatchers.Default) {

        // Load original photo
        val originalBitmap = loadBitmap(photoUri)

        // Create canvas
        val width = Constants.CARD_WIDTH_PX
        val height = Constants.CARD_HEIGHT_STORY_PX
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Draw background photo (scaled and centered)
        drawBackgroundPhoto(canvas, originalBitmap, width, height)

        // Draw glassmorphism overlay
        drawGlassmorphismOverlay(canvas, co2SavedKg, activityType, streak, width, height)

        // Draw footer
        drawFooter(canvas, username, width, height)

        resultBitmap
    }

    /**
     * Generate card with split layout style
     */
    suspend fun generateSplitCard(
        photoUri: Uri,
        co2SavedKg: Double,
        activityType: String,
        streak: Int,
        username: String
    ): Bitmap = withContext(Dispatchers.Default) {

        val originalBitmap = loadBitmap(photoUri)

        val width = Constants.CARD_WIDTH_PX
        val height = Constants.CARD_HEIGHT_FEED_PX
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Photo on top 60%
        val photoHeight = (height * 0.6f).toInt()
        drawScaledPhoto(canvas, originalBitmap, width, photoHeight)

        // Stats on bottom 40%
        drawStatsSection(canvas, co2SavedKg, activityType, streak, username,
            photoHeight, width, height)

        resultBitmap
    }

    /**
     * Generate minimalist card
     */
    suspend fun generateMinimalistCard(
        photoUri: Uri,
        co2SavedKg: Double,
        activityType: String,
        streak: Int
    ): Bitmap = withContext(Dispatchers.Default) {

        val originalBitmap = loadBitmap(photoUri)

        val width = Constants.CARD_WIDTH_PX
        val height = Constants.CARD_HEIGHT_STORY_PX
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Full photo background
        drawBackgroundPhoto(canvas, originalBitmap, width, height)

        // Small badge in corner
        drawMinimalistBadge(canvas, co2SavedKg, activityType, streak, width, height)

        resultBitmap
    }

    /**
     * Load bitmap from URI
     */
    private suspend fun loadBitmap(uri: Uri): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        return when (val result = imageLoader.execute(request)) {
            is SuccessResult -> (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
            else -> throw Exception("Failed to load image")
        }
    }

    /**
     * Draw background photo (scaled and centered)
     */
    private fun drawBackgroundPhoto(canvas: Canvas, bitmap: Bitmap, width: Int, height: Int) {
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            true
        )
        canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
    }

    /**
     * Draw glassmorphism overlay box
     */
    private fun drawGlassmorphismOverlay(
        canvas: Canvas,
        co2: Double,
        activityType: String,
        streak: Int,
        width: Int,
        height: Int
    ) {
        val boxWidth = (width * 0.8f).toInt()
        val boxHeight = 400
        val left = (width - boxWidth) / 2f
        val top = (height - boxHeight) / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#40FFFFFF") // Semi-transparent white
            style = Paint.Style.FILL
        }

        val rect = RectF(left, top, left + boxWidth, top + boxHeight)
        canvas.drawRoundRect(rect, 48f, 48f, paint)

        // Draw border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#60FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(rect, 48f, 48f, borderPaint)

        // Draw text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        // CO2 value
        textPaint.textSize = 120f
        canvas.drawText(
            "+${co2.toCO2String()} kg CO₂",
            width / 2f,
            top + 180f,
            textPaint
        )

        // Activity type
        textPaint.textSize = 60f
        val co2Calculator = CO2Calculator()
        val emoji = co2Calculator.getActivityEmoji(activityType)
        val displayName = co2Calculator.getActivityDisplayName(activityType)
        canvas.drawText(
            "$emoji $displayName",
            width / 2f,
            top + 270f,
            textPaint
        )

        // Streak
        textPaint.textSize = 50f
        canvas.drawText(
            "#$streak Days 🔥",
            width / 2f,
            top + 350f,
            textPaint
        )
    }

    /**
     * Draw footer with username
     */
    private fun drawFooter(canvas: Canvas, username: String, width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            "@$username · Echo Habit",
            width / 2f,
            height - 100f,
            paint
        )
    }

    /**
     * Draw scaled photo for split layout
     */
    private fun drawScaledPhoto(canvas: Canvas, bitmap: Bitmap, width: Int, height: Int) {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        val srcRect = Rect(0, 0, width, height)
        val dstRect = Rect(0, 0, width, height)
        canvas.drawBitmap(scaledBitmap, srcRect, dstRect, null)
    }

    /**
     * Draw stats section for split layout
     */
    private fun drawStatsSection(
        canvas: Canvas,
        co2: Double,
        activityType: String,
        streak: Int,
        username: String,
        top: Int,
        width: Int,
        height: Int
    ) {
        // Background
        val paint = Paint().apply {
            color = Color.parseColor("#D4FF00")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, top.toFloat(), width.toFloat(), height.toFloat(), paint)

        // Text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val centerY = top + ((height - top) / 2f)

        textPaint.textSize = 80f
        canvas.drawText(
            "+${co2.toCO2String()} kg CO₂",
            width / 2f,
            centerY - 60f,
            textPaint
        )

        val co2Calculator = CO2Calculator()
        textPaint.textSize = 50f
        canvas.drawText(
            "${co2Calculator.getActivityEmoji(activityType)} ${co2Calculator.getActivityDisplayName(activityType)}",
            width / 2f,
            centerY + 20f,
            textPaint
        )

        textPaint.textSize = 40f
        canvas.drawText(
            "$streak Days 🔥 • @$username",
            width / 2f,
            centerY + 90f,
            textPaint
        )
    }

    /**
     * Draw minimalist badge
     */
    private fun drawMinimalistBadge(
        canvas: Canvas,
        co2: Double,
        activityType: String,
        streak: Int,
        width: Int,
        height: Int
    ) {
        val badgeSize = 300f
        val margin = 80f
        val left = width - badgeSize - margin
        val top = margin

        // Badge background
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4FF00")
            style = Paint.Style.FILL
        }

        val rect = RectF(left, top, left + badgeSize, top + badgeSize)
        canvas.drawRoundRect(rect, 32f, 32f, paint)

        // Text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val centerX = left + badgeSize / 2
        val centerY = top + badgeSize / 2

        val co2Calculator = CO2Calculator()
        textPaint.textSize = 80f
        canvas.drawText(
            co2Calculator.getActivityEmoji(activityType),
            centerX,
            centerY - 30f,
            textPaint
        )

        textPaint.textSize = 50f
        canvas.drawText(
            "+${co2.toCO2String()}",
            centerX,
            centerY + 50f,
            textPaint
        )

        textPaint.textSize = 35f
        canvas.drawText(
            "🔥 $streak",
            centerX,
            centerY + 100f,
            textPaint
        )
    }
}