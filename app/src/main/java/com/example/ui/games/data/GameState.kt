package com.example.ui.games.data

import kotlinx.serialization.Serializable

@Serializable
data class GameProfile(
    val xp: Int = 0,
    val virtualCoins: Int = 500, // Starts with 500 virtual coins
    val streak: Int = 1,
    val lastPlayedDate: String = "",
    val highScores: Map<String, Int> = emptyMap(),
    val unlockedAchievements: Set<String> = emptySet(),
    val completedDailyChallenges: Set<String> = emptySet(),
    val lastDailyChallengeDate: String = "",
    val cityBuildings: List<CityBuildingState> = emptyList(),
    val lastCityCollectionTimestamp: Long = 0L,
    val totalGamesPlayed: Int = 0
) {
    val level: Int
        get() = when {
            xp >= 5000 -> 50
            xp >= 2000 -> 20
            xp >= 1000 -> 10
            xp >= 500 -> 5
            xp >= 200 -> 3
            xp >= 50 -> 2
            else -> 1
        }

    val levelTitle: String
        get() = when {
            level >= 50 -> "Financial Pro"
            level >= 20 -> "Money Master"
            level >= 10 -> "Smart Saver"
            level >= 5 -> "Saver"
            else -> "Beginner"
        }

    val nextLevelXp: Int
        get() = when {
            level >= 50 -> 10000
            level >= 20 -> 5000
            level >= 10 -> 2000
            level >= 5 -> 1000
            level >= 3 -> 500
            level >= 2 -> 200
            else -> 50
        }

    val levelProgress: Float
        get() {
            val prevXp = when {
                level >= 50 -> 5000
                level >= 20 -> 2000
                level >= 10 -> 1000
                level >= 5 -> 500
                level >= 3 -> 200
                level >= 2 -> 50
                else -> 0
            }
            val range = (nextLevelXp - prevXp).coerceAtLeast(1)
            return ((xp - prevXp).toFloat() / range).coerceIn(0f, 1f)
        }
}

@Serializable
data class CityBuildingState(
    val id: String,
    val typeId: String,
    val level: Int = 1,
    val builtTimestamp: Long = 0L
)

data class BuildingDefinition(
    val typeId: String,
    val name: String,
    val icon: String,
    val description: String,
    val baseCost: Int,
    val baseIncomePerMin: Int,
    val populationBonus: Int,
    val unlockLevel: Int,
    val category: String
)

data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val coinReward: Int
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val gameType: String,
    val targetCount: Int,
    val xpReward: Int,
    val coinReward: Int
)

data class CityEvent(
    val id: String,
    val title: String,
    val icon: String,
    val description: String,
    val coinBonus: Int,
    val xpBonus: Int
)

object GameConstants {
    val ALL_BUILDINGS = listOf(
        BuildingDefinition(
            typeId = "house",
            name = "Suburban House",
            icon = "🏠",
            description = "Provides warm homes for new citizens.",
            baseCost = 100,
            baseIncomePerMin = 4,
            populationBonus = 5,
            unlockLevel = 1,
            category = "Residential"
        ),
        BuildingDefinition(
            typeId = "shop",
            name = "Corner Shop",
            icon = "🏪",
            description = "Local retail store providing essential goods.",
            baseCost = 200,
            baseIncomePerMin = 15,
            populationBonus = 0,
            unlockLevel = 1,
            category = "Commercial"
        ),
        BuildingDefinition(
            typeId = "cafe",
            name = "Cozy Cafe",
            icon = "☕",
            description = "A friendly hangout spot boosting local spirits.",
            baseCost = 350,
            baseIncomePerMin = 30,
            populationBonus = 0,
            unlockLevel = 2,
            category = "Commercial"
        ),
        BuildingDefinition(
            typeId = "park",
            name = "Central Eco Park",
            icon = "🌳",
            description = "Green oasis that raises overall city quality.",
            baseCost = 500,
            baseIncomePerMin = 20,
            populationBonus = 15,
            unlockLevel = 3,
            category = "Public"
        ),
        BuildingDefinition(
            typeId = "bank",
            name = "Community Bank",
            icon = "🏦",
            description = "Teaches smart savings and drives commerce.",
            baseCost = 800,
            baseIncomePerMin = 65,
            populationBonus = 0,
            unlockLevel = 5,
            category = "Financial"
        ),
        BuildingDefinition(
            typeId = "office",
            name = "Tech Office",
            icon = "🏢",
            description = "High-paying digital knowledge jobs.",
            baseCost = 1500,
            baseIncomePerMin = 140,
            populationBonus = 10,
            unlockLevel = 8,
            category = "Commercial"
        ),
        BuildingDefinition(
            typeId = "factory",
            name = "Smart Industry",
            icon = "🏭",
            description = "Eco-friendly production hub powering exports.",
            baseCost = 3000,
            baseIncomePerMin = 300,
            populationBonus = 0,
            unlockLevel = 12,
            category = "Industrial"
        ),
        BuildingDefinition(
            typeId = "exchange",
            name = "Financial Exchange",
            icon = "🏛️",
            description = "The vibrant heartbeat of the city's investments.",
            baseCost = 6000,
            baseIncomePerMin = 650,
            populationBonus = 0,
            unlockLevel = 20,
            category = "Financial"
        ),
        BuildingDefinition(
            typeId = "landmark",
            name = "Prosperity Monument",
            icon = "⭐",
            description = "World-famous architectural wonder.",
            baseCost = 12000,
            baseIncomePerMin = 1500,
            populationBonus = 50,
            unlockLevel = 30,
            category = "Special"
        )
    )

    val ACHIEVEMENTS = listOf(
        AchievementDefinition(
            id = "first_game",
            title = "First Step",
            description = "Play any Money Game for the first time.",
            icon = "🎮",
            xpReward = 25,
            coinReward = 50
        ),
        AchievementDefinition(
            id = "perfect_score",
            title = "Bullseye",
            description = "Achieve a perfect 100% score in any challenge.",
            icon = "🎯",
            xpReward = 50,
            coinReward = 100
        ),
        AchievementDefinition(
            id = "streak_3",
            title = "Daily Habit",
            description = "Maintain a 3-day money learning streak.",
            icon = "🔥",
            xpReward = 60,
            coinReward = 150
        ),
        AchievementDefinition(
            id = "streak_7",
            title = "Week on Fire",
            description = "Maintain a 7-day money learning streak.",
            icon = "⚡",
            xpReward = 150,
            coinReward = 300
        ),
        AchievementDefinition(
            id = "xp_100",
            title = "XP Pioneer",
            description = "Earn a total of 100 XP.",
            icon = "⭐",
            xpReward = 30,
            coinReward = 50
        ),
        AchievementDefinition(
            id = "xp_1000",
            title = "Grandmaster",
            description = "Earn a total of 1,000 XP.",
            icon = "👑",
            xpReward = 200,
            coinReward = 500
        ),
        AchievementDefinition(
            id = "budget_boss",
            title = "Budget Boss",
            description = "Complete 3 Smart Spending scenarios.",
            icon = "🧠",
            xpReward = 75,
            coinReward = 120
        ),
        AchievementDefinition(
            id = "saving_master",
            title = "Saving Master",
            description = "Win 3 Save the Goal challenges.",
            icon = "💰",
            xpReward = 75,
            coinReward = 120
        ),
        AchievementDefinition(
            id = "quiz_whiz",
            title = "Quiz Master",
            description = "Answer 10+ questions correctly in 60-Second Quiz.",
            icon = "⚡",
            xpReward = 75,
            coinReward = 120
        ),
        AchievementDefinition(
            id = "detective_pro",
            title = "Chief Detective",
            description = "Solve 3 Budget Detective mysteries.",
            icon = "🔍",
            xpReward = 75,
            coinReward = 120
        ),
        AchievementDefinition(
            id = "need_want_expert",
            title = "Need vs Want Expert",
            description = "Reach a x5 Combo in Need or Want.",
            icon = "🛒",
            xpReward = 75,
            coinReward = 120
        ),
        AchievementDefinition(
            id = "city_builder",
            title = "City Builder",
            description = "Construct 3 buildings in Build Your Money City.",
            icon = "🏙️",
            xpReward = 100,
            coinReward = 200
        ),
        AchievementDefinition(
            id = "financial_empire",
            title = "Financial Empire",
            description = "Reach City Tier (Level 10) in Money City.",
            icon = "🏰",
            xpReward = 250,
            coinReward = 1000
        )
    )

    val CITY_EVENTS = listOf(
        CityEvent(
            id = "rain",
            title = "🌧️ Refreshing Rain",
            icon = "🌧️",
            description = "City parks and cafes saw a 20% surge in visitors today!",
            coinBonus = 80,
            xpBonus = 20
        ),
        CityEvent(
            id = "festival",
            title = "🎉 City Market Festival",
            icon = "🎉",
            description = "Citizens celebrated the annual financial wellness fair!",
            coinBonus = 150,
            xpBonus = 35
        ),
        CityEvent(
            id = "investor",
            title = "💼 Angel Investment Grant",
            icon = "💼",
            description = "An investment group recognized your sustainable city planning!",
            coinBonus = 250,
            xpBonus = 50
        ),
        CityEvent(
            id = "construction",
            title = "🏗️ Construction Efficiency Bonus",
            icon = "🏗️",
            description = "Local guilds finished projects ahead of schedule!",
            coinBonus = 100,
            xpBonus = 25
        ),
        CityEvent(
            id = "solar",
            title = "☀️ Clean Energy Dividend",
            icon = "☀️",
            description = "Green energy initiatives returned public utility savings!",
            coinBonus = 120,
            xpBonus = 30
        )
    )
}
