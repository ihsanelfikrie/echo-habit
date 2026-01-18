package com.echohabit.app.util

object Constants {

    // App Info
    const val APP_NAME = "Echo Habit"
    const val APP_VERSION = "2.0.0"
    const val DEVELOPER_NAME = "Muhammad Nur Ihsan"
    const val DEVELOPER_NIM = "230104040214"

    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ACTIVITIES = "activities"
    const val COLLECTION_STATS = "stats"
    const val COLLECTION_BADGES = "badges"

    // Firebase Storage Paths
    const val STORAGE_PHOTOS = "photos"
    const val STORAGE_CARDS = "cards"
    const val STORAGE_AVATARS = "avatars"

    // Activity Categories
    const val CATEGORY_MOVE_GREEN = "move_green"
    const val CATEGORY_EAT_CLEAN = "eat_clean"
    const val CATEGORY_CUT_WASTE = "cut_waste"
    const val CATEGORY_SAVE_ENERGY = "save_energy"

    // Activity Types
    object ActivityType {
        // Move Green
        const val BIKE = "bike"
        const val WALK = "walk"
        const val PUBLIC_TRANSPORT = "public_transport"

        // Eat Clean
        const val VEGAN_MEAL = "vegan_meal"
        const val VEGETARIAN_MEAL = "vegetarian_meal"
        const val LOCAL_FOOD = "local_food"

        // Cut Waste
        const val USE_TUMBLER = "use_tumbler"
        const val TOTE_BAG = "tote_bag"
        const val NO_PLASTIC_STRAW = "no_plastic_straw"
        const val RECYCLING = "recycling"

        // Save Energy
        const val UNPLUG_DEVICES = "unplug_devices"
        const val LED_LIGHTS = "led_lights"
        const val AC_OFF = "ac_off"
    }

    // Points & CO2 Values
    data class ActivityValue(
        val points: Int,
        val co2SavedKg: Double
    )

    val ACTIVITY_VALUES = mapOf(
        ActivityType.BIKE to ActivityValue(30, 3.0),
        ActivityType.PUBLIC_TRANSPORT to ActivityValue(25, 2.5),
        ActivityType.WALK to ActivityValue(15, 1.5),
        ActivityType.VEGAN_MEAL to ActivityValue(20, 2.0),
        ActivityType.VEGETARIAN_MEAL to ActivityValue(15, 1.5),
        ActivityType.LOCAL_FOOD to ActivityValue(12, 1.2),
        ActivityType.USE_TUMBLER to ActivityValue(10, 0.5),
        ActivityType.TOTE_BAG to ActivityValue(10, 0.5),
        ActivityType.NO_PLASTIC_STRAW to ActivityValue(5, 0.2),
        ActivityType.RECYCLING to ActivityValue(8, 0.8),
        ActivityType.UNPLUG_DEVICES to ActivityValue(8, 0.8),
        ActivityType.LED_LIGHTS to ActivityValue(10, 1.0),
        ActivityType.AC_OFF to ActivityValue(15, 1.5)
    )

    // Levels
    data class Level(
        val name: String,
        val emoji: String,
        val minPoints: Int,
        val maxPoints: Int
    )

    val LEVELS = listOf(
        Level("Seedling", "🌱", 0, 99),
        Level("Sprout", "🌿", 100, 499),
        Level("Plant", "🪴", 500, 999),
        Level("Tree", "🌳", 1000, 2499),
        Level("Forest", "🌲", 2500, Int.MAX_VALUE)
    )

    // Badges
    data class BadgeInfo(
        val id: String,
        val name: String,
        val emoji: String,
        val description: String,
        val requirement: Int
    )

    val BADGES = listOf(
        BadgeInfo("fire_starter", "Fire Starter", "🔥", "7-day streak", 7),
        BadgeInfo("pedal_power", "Pedal Power", "🚴", "10 bike activities", 10),
        BadgeInfo("plant_pioneer", "Plant Pioneer", "🥗", "20 vegan meals", 20),
        BadgeInfo("waste_warrior", "Waste Warrior", "♻️", "50 zero-waste actions", 50),
        BadgeInfo("planet_hero", "Planet Hero", "🌍", "100 total activities", 100),
        BadgeInfo("consistency_king", "Consistency King", "👑", "30-day streak", 30),
        BadgeInfo("eco_legend", "Eco Legend", "⭐", "365-day streak", 365)
    )

    // Card Styles
    const val CARD_STYLE_GLASSMORPHISM = "glassmorphism"
    const val CARD_STYLE_SPLIT = "split"
    const val CARD_STYLE_MINIMALIST = "minimalist"

    // Image Specs
    const val MAX_PHOTO_SIZE_MB = 5
    const val CARD_WIDTH_PX = 1080
    const val CARD_HEIGHT_STORY_PX = 1920
    const val CARD_HEIGHT_FEED_PX = 1080
    const val IMAGE_QUALITY = 90

    // Streak
    const val STREAK_REMINDER_HOUR = 20 // 8 PM
    const val STREAK_FREEZE_LIMIT = 1 // 1 freeze per week

    // Share Platforms
    const val SHARE_INSTAGRAM = "instagram"
    const val SHARE_TIKTOK = "tiktok"
    const val SHARE_WHATSAPP = "whatsapp"

    // Navigation Routes
    object Routes {
        const val SPLASH = "splash"
        const val LOGIN = "login"
        const val HOME = "home"
        const val UPLOAD = "upload"
        const val CARD_PREVIEW = "card_preview"
        const val PROFILE = "profile"
        const val STATS = "stats"
    }

    // Preferences Keys
    object Prefs {
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_ID = "user_id"
        const val DARK_MODE = "dark_mode"
        const val NOTIFICATION_ENABLED = "notification_enabled"
    }
}