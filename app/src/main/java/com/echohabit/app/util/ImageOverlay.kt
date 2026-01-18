package com.echohabit.app.util

import android.graphics.*

/**
 * Utility class for image overlay operations
 */
object ImageOverlay {

    /**
     * Apply blur effect to bitmap
     */
    fun applyBlur(bitmap: Bitmap, radius: Float = 25f): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blurred = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(blurred)

        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG

        // Draw original bitmap
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return blurred
    }

    /**
     * Draw text with shadow
     */
    fun drawTextWithShadow(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        textSize: Float,
        textColor: Int = Color.WHITE
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            this.textSize = textSize
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            setShadowLayer(8f, 0f, 4f, Color.BLACK)
        }

        canvas.drawText(text, x, y, paint)
    }

    /**
     * Create rounded rectangle path
     */
    fun createRoundedRectPath(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float
    ): Path {
        val path = Path()
        path.addRoundRect(
            RectF(left, top, right, bottom),
            radius,
            radius,
            Path.Direction.CW
        )
        return path
    }
}