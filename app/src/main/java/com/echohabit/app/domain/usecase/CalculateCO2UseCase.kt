package com.echohabit.app.domain.usecase

import com.echohabit.app.util.CO2Calculator

class CalculateCO2UseCase(
    private val co2Calculator: CO2Calculator
) {

    data class CO2Data(
        val co2SavedKg: Double,
        val points: Int,
        val treesEquivalent: Int,
        val displayName: String,
        val emoji: String
    )

    /**
     * Calculate all CO2 related data for an activity
     */
    operator fun invoke(activityType: String): CO2Data {
        val co2 = co2Calculator.calculateCO2(activityType)
        val points = co2Calculator.calculatePoints(activityType)
        val trees = co2Calculator.co2ToTrees(co2)
        val displayName = co2Calculator.getActivityDisplayName(activityType)
        val emoji = co2Calculator.getActivityEmoji(activityType)

        return CO2Data(
            co2SavedKg = co2,
            points = points,
            treesEquivalent = trees,
            displayName = displayName,
            emoji = emoji
        )
    }

    /**
     * Calculate cumulative CO2 data
     */
    fun calculateCumulative(activities: List<String>): CO2Data {
        var totalCO2 = 0.0
        var totalPoints = 0

        activities.forEach { activityType ->
            totalCO2 += co2Calculator.calculateCO2(activityType)
            totalPoints += co2Calculator.calculatePoints(activityType)
        }

        return CO2Data(
            co2SavedKg = totalCO2,
            points = totalPoints,
            treesEquivalent = co2Calculator.co2ToTrees(totalCO2),
            displayName = "Total Impact",
            emoji = "🌍"
        )
    }
}