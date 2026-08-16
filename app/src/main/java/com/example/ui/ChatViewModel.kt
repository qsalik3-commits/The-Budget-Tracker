package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.BudgetRepository
import com.example.data.TransactionWithCategory
import com.example.data.GoalEntity
import com.example.gemini.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

data class ChatMessage(
    val role: String,
    val text: String
)

class ChatViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    init {
        _messages.value = listOf(
            ChatMessage("model", "Hello! I am your AI financial assistant. I can analyze your budget, answer questions about your income, expenses, and savings, or give financial advice. How can I help you today?")
        )
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    fun sendMessage(userText: String, currencySymbol: String = "$") {
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(ChatMessage("user", userText))
        _messages.value = currentMessages

        _isLoading.value = true

        viewModelScope.launch {
            val apiKeyToUse = _customApiKey.value.ifBlank { BuildConfig.GEMINI_API_KEY }.trim()
            val isValidKey = apiKeyToUse.isNotBlank() && apiKeyToUse != "MY_GEMINI_API_KEY"

            var aiResponseSuccess = false
            if (isValidKey) {
                try {
                    val contents = currentMessages.takeLast(6).map { msg ->
                        Content(
                            role = if (msg.role == "user") "user" else "model",
                            parts = listOf(Part(text = msg.text))
                        )
                    }

                    val request = GenerateContentRequest(
                        contents = contents,
                        systemInstruction = Content(
                            parts = listOf(Part(text = "You are a friendly, professional AI financial advisor helping users manage budgets, investments, and savings goals. Currency is $currencySymbol. Give concise, actionable advice."))
                        )
                    )

                    val response = RetrofitClient.service.generateContent(apiKeyToUse, request)
                    val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!replyText.isNullOrBlank()) {
                        _messages.value = _messages.value + ChatMessage("model", replyText)
                        aiResponseSuccess = true
                    }
                } catch (e: Exception) {
                    // Fall back to smart local response on API failure
                }
            }

            if (!aiResponseSuccess) {
                // Smart local financial assistant logic using live database
                val localReply = generateSmartLocalResponse(userText, currencySymbol)
                _messages.value = _messages.value + ChatMessage("model", localReply)
            }

            _isLoading.value = false
        }
    }

    private suspend fun generateSmartLocalResponse(query: String, symbol: String): String {
        val q = query.lowercase(Locale.ROOT)
        val transactions = repository.allTransactions.first()
        val goals = repository.allGoals.first()

        val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
        val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense

        val formatVal = { amt: Double -> "$symbol${String.format(Locale.US, "%,.2f", Math.abs(amt)).removeSuffix(".00")}" }

        return when {
            q.contains("balance") || q.contains("income") || q.contains("expense") || q.contains("budget") || q.contains("summary") || q.contains("how much") -> {
                val topExpense = transactions.filter { !it.isIncome }
                    .groupBy { it.categoryName }
                    .mapValues { it.value.sumOf { t -> t.amount } }
                    .maxByOrNull { it.value }

                buildString {
                    append("📊 **Financial Overview:**\n")
                    append("• **Total Income:** ${formatVal(totalIncome)}\n")
                    append("• **Total Expenses:** ${formatVal(totalExpense)}\n")
                    append("• **Net Balance:** ${formatVal(netBalance)}\n\n")
                    if (topExpense != null) {
                        append("💡 Your highest spending category is **${topExpense.key}** at **${formatVal(topExpense.value)}**.")
                    } else {
                        append("💡 Tip: Add transactions to track your spending habits across categories!")
                    }
                }
            }
            q.contains("goal") || q.contains("saving") -> {
                if (goals.isEmpty()) {
                    "🎯 You don't have any active savings goals yet. Go to the **Goals** tab to set a target for a vacation, emergency fund, or major purchase!"
                } else {
                    val goalsSummary = goals.joinToString("\n") { g ->
                        val pct = if (g.targetAmount > 0) ((g.currentAmount / g.targetAmount) * 100).toInt() else 0
                        "• **${g.name}**: ${formatVal(g.currentAmount)} / ${formatVal(g.targetAmount)} ($pct%)"
                    }
                    "🎯 **Savings Goals Progress:**\n$goalsSummary\n\nKeep adding contributions to reach your targets faster!"
                }
            }
            q.contains("sip") || q.contains("invest") || q.contains("mutual fund") || q.contains("emi") || q.contains("calculator") -> {
                "🧮 You can calculate monthly investment returns or loan interest easily! Switch to the **Calc** tab in the bottom menu to test out SIP and EMI projections."
            }
            q.contains("save") || q.contains("tip") || q.contains("advice") || q.contains("help") -> {
                "💡 **Smart Money Saving Tips:**\n" +
                "1. **Follow the 50/30/20 Rule**: 50% for Needs, 30% for Wants, and 20% for Savings.\n" +
                "2. **Track Daily Expenses**: Log even small daily spends to identify impulse purchases.\n" +
                "3. **Build an Emergency Fund**: Aim to save 3 to 6 months of essential living expenses.\n" +
                "4. **Automate Savings**: Set aside savings as soon as income comes in, rather than saving what's left over."
            }
            else -> {
                "I'm here to help you manage your finances! Here are a few things you can ask me:\n" +
                "• \"What is my budget summary?\"\n" +
                "• \"How are my savings goals going?\"\n" +
                "• \"Give me tips to save money\"\n" +
                "• \"How do SIP and EMI calculators work?\""
            }
        }
    }
}

