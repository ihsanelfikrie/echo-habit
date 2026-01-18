package com.echohabit.app.util

class CO2Calculator {

    /**
     * Calculate CO2 saved based on activity type
     */
    fun calculateCO2(activityType: String): Double {
        return Constants.ACTIVITY_VALUES[activityType]?.co2SavedKg ?: 0.0
    }

    /**
     * Calculate points based on activity type
     */
    fun calculatePoints(activityType: String): Int {
        return Constants.ACTIVITY_VALUES[activityType]?.points ?: 0
    }

    /**
     * Convert CO2 kg to equivalent trees
     * Average: 1 tree absorbs ~22 kg CO2 per year
     */
    fun co2ToTrees(co2Kg: Double): Int {
        return (co2Kg * 365 / 22).toInt()
    }

    /**
     * Get level based on total points
     */
    fun getLevel(totalPoints: Int): Constants.Level {
        return Constants.LEVELS.find {
            totalPoints in it.minPoints..it.maxPoints
        } ?: Constants.LEVELS.first()
    }

    /**
     * Calculate progress to next level (0-100)
     */
    fun getLevelProgress(totalPoints: Int): Float {
        val currentLevel = getLevel(totalPoints)
        if (currentLevel == Constants.LEVELS.last()) return 100f

        val pointsInLevel = totalPoints - currentLevel.minPoints
        val levelRange = currentLevel.maxPoints - currentLevel.minPoints + 1
        return (pointsInLevel.toFloat() / levelRange * 100).coerceIn(0f, 100f)
    }

    /**
     * Get activity display name
     */
    fun getActivityDisplayName(activityType: String): String {
        return when (activityType) {
            Constants.ActivityType.BIKE -> "Biked"
            Constants.ActivityType.WALK -> "Walked"
            Constants.ActivityType.PUBLIC_TRANSPORT -> "Public Transport"
            Constants.ActivityType.VEGAN_MEAL -> "Vegan Meal"
            Constants.ActivityType.VEGETARIAN_MEAL -> "Vegetarian Meal"
            Constants.ActivityType.LOCAL_FOOD -> "Local Food"
            Constants.ActivityType.USE_TUMBLER -> "Used Tumbler"
            Constants.ActivityType.TOTE_BAG -> "Used Tote Bag"
            Constants.ActivityType.NO_PLASTIC_STRAW -> "No Plastic Straw"
            Constants.ActivityType.RECYCLING -> "Recycled"
            Constants.ActivityType.UNPLUG_DEVICES -> "Unplugged Devices"
            Constants.ActivityType.LED_LIGHTS -> "Used LED Lights"
            Constants.ActivityType.AC_OFF -> "Turned AC Off"
            else -> "Unknown Activity"
        }
    }

    /**
     * Get activity emoji
     */
    fun getActivityEmoji(activityType: String): String {
        return when (activityType) {
            Constants.ActivityType.BIKE -> "🚴"
            Constants.ActivityType.WALK -> "🚶"
            Constants.ActivityType.PUBLIC_TRANSPORT -> "🚌"
            Constants.ActivityType.VEGAN_MEAL -> "🥗"
            Constants.ActivityType.VEGETARIAN_MEAL -> "🥙"
            Constants.ActivityType.LOCAL_FOOD -> "🍽️"
            Constants.ActivityType.USE_TUMBLER -> "🥤"
            Constants.ActivityType.TOTE_BAG -> "🛍️"
            Constants.ActivityType.NO_PLASTIC_STRAW -> "🥤"
            Constants.ActivityType.RECYCLING -> "♻️"
            Constants.ActivityType.UNPLUG_DEVICES -> "🔌"
            Constants.ActivityType.LED_LIGHTS -> "💡"
            Constants.ActivityType.AC_OFF -> "❄️"
            else -> "🌍"
        }
    }
}