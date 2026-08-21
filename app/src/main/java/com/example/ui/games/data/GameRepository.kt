package com.example.ui.games.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("money_games_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val _gameState = MutableStateFlow(loadGameState())
    val gameState: StateFlow<GameProfile> = _gameState.asStateFlow()

    private fun loadGameState(): GameProfile {
        val raw = prefs.getString("game_profile_v1", null)
        val state = if (raw != null) {
            try {
                json.decodeFromString<GameProfile>(raw)
            } catch (e: Exception) {
                GameProfile()
            }
        } else {
            // Initial default state with 1 starting building so city has life
            GameProfile(
                virtualCoins = 500,
                cityBuildings = listOf(
                    CityBuildingState(id = "init_house_1", typeId = "house", level = 1, builtTimestamp = System.currentTimeMillis()),
                    CityBuildingState(id = "init_shop_1", typeId = "shop", level = 1, builtTimestamp = System.currentTimeMillis())
                ),
                lastCityCollectionTimestamp = System.currentTimeMillis()
            )
        }
        return state
    }

    private fun saveGameState(state: GameProfile) {
        _gameState.value = state
        try {
            val serialized = json.encodeToString(state)
            prefs.edit().putString("game_profile_v1", serialized).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addRewards(xpGained: Int, coinsGained: Int, gameId: String? = null, score: Int = 0): GameRewardResult {
        val current = _gameState.value
        val oldLevel = current.level
        val newXp = current.xp + xpGained
        val newCoins = (current.virtualCoins + coinsGained).coerceAtLeast(0)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Calculate streak
        val (newStreak, newLastDate) = calculateStreak(current.lastPlayedDate, today, current.streak)

        // Track high score if applicable
        val updatedHighScores = current.highScores.toMutableMap()
        if (gameId != null && score > (updatedHighScores[gameId] ?: 0)) {
            updatedHighScores[gameId] = score
        }

        // Check new achievements
        val newlyUnlocked = mutableListOf<AchievementDefinition>()
        val currentAchievements = current.unlockedAchievements.toMutableSet()

        fun grantAchievement(id: String) {
            if (!currentAchievements.contains(id)) {
                currentAchievements.add(id)
                GameConstants.ACHIEVEMENTS.find { it.id == id }?.let {
                    newlyUnlocked.add(it)
                }
            }
        }

        // Check first game
        grantAchievement("first_game")

        if (score == 100) {
            grantAchievement("perfect_score")
        }
        if (newStreak >= 3) {
            grantAchievement("streak_3")
        }
        if (newStreak >= 7) {
            grantAchievement("streak_7")
        }
        if (newXp >= 100) {
            grantAchievement("xp_100")
        }
        if (newXp >= 1000) {
            grantAchievement("xp_1000")
        }

        val bonusXpFromAchievements = newlyUnlocked.sumOf { it.xpReward }
        val bonusCoinsFromAchievements = newlyUnlocked.sumOf { it.coinReward }

        val finalState = current.copy(
            xp = newXp + bonusXpFromAchievements,
            virtualCoins = newCoins + bonusCoinsFromAchievements,
            streak = newStreak,
            lastPlayedDate = newLastDate,
            highScores = updatedHighScores,
            unlockedAchievements = currentAchievements,
            totalGamesPlayed = current.totalGamesPlayed + 1
        )

        saveGameState(finalState)

        val leveledUp = finalState.level > oldLevel

        return GameRewardResult(
            xpGained = xpGained + bonusXpFromAchievements,
            coinsGained = coinsGained + bonusCoinsFromAchievements,
            leveledUp = leveledUp,
            newLevel = finalState.level,
            newLevelTitle = finalState.levelTitle,
            newAchievements = newlyUnlocked
        )
    }

    private fun calculateStreak(lastPlayed: String, today: String, currentStreak: Int): Pair<Int, String> {
        if (lastPlayed.isBlank()) {
            return Pair(1, today)
        }
        if (lastPlayed == today) {
            return Pair(currentStreak.coerceAtLeast(1), today)
        }
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val lastDate = format.parse(lastPlayed)
            val nowDate = format.parse(today)
            val diffDays = (nowDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
            if (diffDays == 1L) {
                Pair(currentStreak + 1, today)
            } else if (diffDays > 1L) {
                Pair(1, today)
            } else {
                Pair(currentStreak, today)
            }
        } catch (e: Exception) {
            Pair(1, today)
        }
    }

    fun completeDailyChallenge(challengeId: String, xpReward: Int, coinReward: Int): GameRewardResult {
        val current = _gameState.value
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updatedSet = if (current.lastDailyChallengeDate == today) {
            current.completedDailyChallenges.toMutableSet()
        } else {
            mutableSetOf()
        }
        if (!updatedSet.contains(challengeId)) {
            updatedSet.add(challengeId)
            val updated = current.copy(
                completedDailyChallenges = updatedSet,
                lastDailyChallengeDate = today
            )
            saveGameState(updated)
            return addRewards(xpReward, coinReward)
        }
        return GameRewardResult()
    }

    // Money City Functions
    fun buildCityStructure(typeId: String): Boolean {
        val current = _gameState.value
        val definition = GameConstants.ALL_BUILDINGS.find { it.typeId == typeId } ?: return false
        if (current.virtualCoins < definition.baseCost) return false

        val newBuilding = CityBuildingState(
            id = "b_${System.currentTimeMillis()}_${(100..999).random()}",
            typeId = typeId,
            level = 1,
            builtTimestamp = System.currentTimeMillis()
        )

        val updatedBuildings = current.cityBuildings + newBuilding
        val updatedCoins = current.virtualCoins - definition.baseCost
        val updatedXp = current.xp + (definition.baseCost / 10).coerceAtLeast(10)

        val currentAchievements = current.unlockedAchievements.toMutableSet()
        if (updatedBuildings.size >= 3) {
            currentAchievements.add("city_builder")
        }

        saveGameState(
            current.copy(
                virtualCoins = updatedCoins,
                xp = updatedXp,
                cityBuildings = updatedBuildings,
                unlockedAchievements = currentAchievements
            )
        )
        return true
    }

    fun upgradeCityStructure(buildingId: String): Boolean {
        val current = _gameState.value
        val building = current.cityBuildings.find { it.id == buildingId } ?: return false
        val definition = GameConstants.ALL_BUILDINGS.find { it.typeId == building.typeId } ?: return false
        val upgradeCost = definition.baseCost * (building.level + 1) / 2

        if (current.virtualCoins < upgradeCost || building.level >= 5) return false

        val updatedBuildings = current.cityBuildings.map {
            if (it.id == buildingId) it.copy(level = it.level + 1) else it
        }

        val updatedCoins = current.virtualCoins - upgradeCost
        val updatedXp = current.xp + (upgradeCost / 10).coerceAtLeast(15)

        saveGameState(
            current.copy(
                virtualCoins = updatedCoins,
                xp = updatedXp,
                cityBuildings = updatedBuildings
            )
        )
        return true
    }

    fun collectCityRevenue(): Int {
        val current = _gameState.value
        val now = System.currentTimeMillis()
        val lastTimestamp = if (current.lastCityCollectionTimestamp == 0L) now - 60000 else current.lastCityCollectionTimestamp
        val elapsedMinutes = ((now - lastTimestamp) / 60000).coerceIn(0, 720) // cap at 12 hours

        var totalIncome = 0
        current.cityBuildings.forEach { b ->
            val def = GameConstants.ALL_BUILDINGS.find { it.typeId == b.typeId }
            if (def != null) {
                val incomeRate = def.baseIncomePerMin * b.level
                totalIncome += (incomeRate * elapsedMinutes.toInt()).coerceAtLeast(0)
            }
        }

        if (totalIncome > 0) {
            saveGameState(
                current.copy(
                    virtualCoins = current.virtualCoins + totalIncome,
                    lastCityCollectionTimestamp = now
                )
            )
        } else {
            saveGameState(current.copy(lastCityCollectionTimestamp = now))
        }

        return totalIncome
    }

    fun triggerRandomCityEvent(): CityEvent {
        val event = GameConstants.CITY_EVENTS.random()
        val current = _gameState.value
        saveGameState(
            current.copy(
                virtualCoins = current.virtualCoins + event.coinBonus,
                xp = current.xp + event.xpBonus
            )
        )
        return event
    }
}

data class GameRewardResult(
    val xpGained: Int = 0,
    val coinsGained: Int = 0,
    val leveledUp: Boolean = false,
    val newLevel: Int = 1,
    val newLevelTitle: String = "Beginner",
    val newAchievements: List<AchievementDefinition> = emptyList()
)
