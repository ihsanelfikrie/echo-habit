package com.echohabit.app.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    /**
     * Get current date string (YYYY-MM-DD)
     */
    fun getCurrentDateString(): String {
        return dateFormat.format(Date())
    }

    /**
     * Convert Timestamp to date string
     */
    fun timestampToDateString(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        return dateFormat.format(timestamp.toDate())
    }

    /**
     * Get relative time string (e.g., "2h ago", "1d ago")
     */
    fun getRelativeTimeString(timestamp: Timestamp?): String {
        if (timestamp == null) return ""

        val now = System.currentTimeMillis()
        val time = timestamp.toDate().time
        val diff = now - time

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "${minutes}m ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "${hours}h ago"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "${days}d ago"
            }
            else -> dateFormat.format(timestamp.toDate())
        }
    }

    /**
     * Format timestamp to readable date time
     */
    fun formatDateTime(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        return dateTimeFormat.format(timestamp.toDate())
    }

    /**
     * Check if timestamp is today
     */
    fun isToday(timestamp: Timestamp?): Boolean {
        if (timestamp == null) return false
        val today = getCurrentDateString()
        val dateString = timestampToDateString(timestamp)
        return today == dateString
    }

    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return Timestamp(calendar.time)
    }

    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return Timestamp(calendar.time)
    }

    /**
     * Calculate days between two timestamps
     */
    fun daysBetween(start: Timestamp?, end: Timestamp?): Int {
        if (start == null || end == null) return 0
        val diff = end.toDate().time - start.toDate().time
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }
}
