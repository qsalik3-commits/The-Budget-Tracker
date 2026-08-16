package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

data class CurrencyInfo(
    val code: String,
    val symbol: String,
    val name: String
) {
    fun format(amount: Double): String {
        val absAmount = Math.abs(amount)
        val formatted = String.format(Locale.US, "%,.2f", absAmount)
        val clean = if (formatted.endsWith(".00")) formatted.dropLast(3) else formatted
        val sign = if (amount < 0) "-" else ""
        return "$sign$symbol$clean"
    }
}

val AVAILABLE_CURRENCIES = listOf(
    CurrencyInfo("USD", "$", "US Dollar ($)"),
    CurrencyInfo("INR", "₹", "Indian Rupee (₹)"),
    CurrencyInfo("EUR", "€", "Euro (€)"),
    CurrencyInfo("GBP", "£", "British Pound (£)"),
    CurrencyInfo("JPY", "¥", "Japanese Yen (¥)"),
    CurrencyInfo("AUD", "A$", "Australian Dollar (A$)"),
    CurrencyInfo("CAD", "C$", "Canadian Dollar (C$)"),
    CurrencyInfo("AED", "AED ", "UAE Dirham (AED)")
)

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _selectedCurrency = MutableStateFlow(AVAILABLE_CURRENCIES[0]) // Default to USD $
    val selectedCurrency: StateFlow<CurrencyInfo> = _selectedCurrency.asStateFlow()

    private val _aiChallenge = MutableStateFlow<String?>(null)
    val aiChallenge: StateFlow<String?> = _aiChallenge.asStateFlow()

    fun fetchAIChallenge() {
        viewModelScope.launch {
            if (_aiChallenge.value != null) return@launch
            
            val transactions = repository.allTransactions.first()
            if (transactions.isEmpty()) {
                _aiChallenge.value = "Log your first expense to get a personalized savings challenge!"
                return@launch
            }
            
            val recentExpenses = transactions.filter { !it.isIncome }.take(10)
            if (recentExpenses.isEmpty()) {
                _aiChallenge.value = "You have no recent expenses. Keep up the great saving!"
                return@launch
            }
            
            val expenseSummary = recentExpenses.joinToString(", ") { "${it.note}: ${it.amount}" }
            
            val apiKey = com.example.BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                _aiChallenge.value = "The No-Uber Week: Can you trade 3 rides for public transit to save $40?"
                return@launch
            }
            
            try {
                val prompt = "Based on these recent expenses: [$expenseSummary]. Generate a 1-sentence personalized daily savings challenge for the user. Example: 'The No-Uber Week: Can you trade 3 rides for public transit to save $40?' Keep it engaging, achievable, and under 15 words. Don't use quotes."
                val request = com.example.gemini.GenerateContentRequest(
                    contents = listOf(com.example.gemini.Content(parts = listOf(com.example.gemini.Part(text = prompt))))
                )
                val response = com.example.gemini.RetrofitClient.service.generateContent(apiKey, request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!reply.isNullOrBlank()) {
                    _aiChallenge.value = reply
                } else {
                    _aiChallenge.value = "Challenge: Cook dinner at home tonight instead of eating out to save $15!"
                }
            } catch (e: Exception) {
                _aiChallenge.value = "Challenge: Review your subscriptions today and cancel one you don't use!"
            }
        }
    }
    fun setCurrency(currency: CurrencyInfo) {
        _selectedCurrency.value = currency
    }

    fun formatAmount(amount: Double): String {
        return _selectedCurrency.value.format(amount)
    }

    val allCategories = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTransactions = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBudgets = repository.allBudgets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allGoals = repository.allGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allHabitLogs = repository.allHabitLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleHabit(habitName: String, date: String, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                repository.insertHabitLog(HabitLogEntity(habitName = habitName, date = date))
            } else {
                repository.deleteHabitLog(habitName, date)
            }
        }
    }

    fun addTransaction(amount: Double, categoryId: Int, note: String, timestamp: Long, isIncome: Boolean) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    categoryId = categoryId,
                    note = note,
                    timestamp = timestamp,
                    isIncome = isIncome
                )
            )
        }
    }

    fun addCategory(name: String, isIncome: Boolean, color: Long) {
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name,
                    isIncome = isIncome,
                    color = color
                )
            )
        }
    }

    fun addBudget(categoryId: Int, limit: Double) {
        viewModelScope.launch {
            repository.insertBudget(BudgetEntity(categoryId, limit))
        }
    }

    fun addGoal(name: String, targetAmount: Double) {
        viewModelScope.launch {
            repository.insertGoal(GoalEntity(name = name, targetAmount = targetAmount, currentAmount = 0.0))
        }
    }

    fun addGoalProgress(goalId: Int, amount: Double) {
        viewModelScope.launch {
            repository.addGoalProgress(goalId, amount)
        }
    }
}

