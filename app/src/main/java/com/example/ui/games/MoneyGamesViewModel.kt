package com.example.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ui.games.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoneyGamesViewModel(private val repository: GameRepository) : ViewModel() {

    val gameState: StateFlow<GameProfile> = repository.gameState

    private val _rewardPopup = MutableStateFlow<GameRewardResult?>(null)
    val rewardPopup: StateFlow<GameRewardResult?> = _rewardPopup.asStateFlow()

    private val _activeCityEvent = MutableStateFlow<CityEvent?>(null)
    val activeCityEvent: StateFlow<CityEvent?> = _activeCityEvent.asStateFlow()

    val dailyChallenges: List<DailyChallenge> = listOf(
        DailyChallenge(
            id = "dc_quiz",
            title = "Quiz Champion",
            description = "Answer at least 5 questions correctly in the 60-Second Quiz.",
            gameType = "quiz",
            targetCount = 5,
            xpReward = 40,
            coinReward = 80
        ),
        DailyChallenge(
            id = "dc_spending",
            title = "Smart Budgeting",
            description = "Score 80% or higher in any Smart Spending scenario.",
            gameType = "spending",
            targetCount = 1,
            xpReward = 50,
            coinReward = 100
        ),
        DailyChallenge(
            id = "dc_need_want",
            title = "Needs vs Wants Master",
            description = "Categorize 10 items accurately in Need or Want.",
            gameType = "need_want",
            targetCount = 10,
            xpReward = 35,
            coinReward = 70
        ),
        DailyChallenge(
            id = "dc_city",
            title = "City Prosperity",
            description = "Collect city earnings or construct a new building in Money City.",
            gameType = "city",
            targetCount = 1,
            xpReward = 45,
            coinReward = 90
        )
    )

    fun recordGameFinished(gameId: String, score: Int, xp: Int, coins: Int) {
        viewModelScope.launch {
            val result = repository.addRewards(
                xpGained = xp,
                coinsGained = coins,
                gameId = gameId,
                score = score
            )
            if (result.leveledUp || result.newAchievements.isNotEmpty() || xp > 0 || coins > 0) {
                _rewardPopup.value = result
            }
        }
    }

    fun claimDailyChallenge(challenge: DailyChallenge) {
        viewModelScope.launch {
            val result = repository.completeDailyChallenge(
                challengeId = challenge.id,
                xpReward = challenge.xpReward,
                coinReward = challenge.coinReward
            )
            if (result.xpGained > 0 || result.coinsGained > 0) {
                _rewardPopup.value = result
            }
        }
    }

    fun dismissRewardPopup() {
        _rewardPopup.value = null
    }

    fun buildCityStructure(typeId: String): Boolean {
        return repository.buildCityStructure(typeId)
    }

    fun upgradeCityStructure(buildingId: String): Boolean {
        return repository.upgradeCityStructure(buildingId)
    }

    fun collectCityRevenue(): Int {
        val collected = repository.collectCityRevenue()
        if (collected > 0) {
            _rewardPopup.value = GameRewardResult(
                xpGained = (collected / 20).coerceAtLeast(5),
                coinsGained = collected
            )
        }
        return collected
    }

    fun triggerRandomCityEvent() {
        val event = repository.triggerRandomCityEvent()
        _activeCityEvent.value = event
    }

    fun dismissCityEvent() {
        _activeCityEvent.value = null
    }
}

class MoneyGamesViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoneyGamesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoneyGamesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
