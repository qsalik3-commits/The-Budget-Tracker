package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TransactionWithCategory
import com.example.ui.AVAILABLE_CURRENCIES
import com.example.ui.BudgetViewModel
import com.example.ui.CurrencyInfo
import com.example.ui.theme.ExpenseIconBg
import com.example.ui.theme.ExpenseIconFg
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeIconBg
import com.example.ui.theme.IncomeIconFg
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BudgetViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val aiChallenge by viewModel.aiChallenge.collectAsStateWithLifecycle()
    var showCurrencyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAIChallenge()
    }

    val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Budget Tracker", fontWeight = FontWeight.Normal) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    // Currency Selection Badge Button
                    Surface(
                        onClick = { showCurrencyDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedCurrency.symbol,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedCurrency.code,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI Assistant", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = Color(0xFFD0BCFF),
                contentColor = Color(0xFF381E72),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BalanceCard(
                    balance = balance,
                    income = totalIncome,
                    expense = totalExpense,
                    currency = selectedCurrency
                )
                Spacer(modifier = Modifier.height(16.dp))
                GamificationBanner(transactions = transactions)
                
                if (aiChallenge != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🤖", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Today's AI Task",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = aiChallenge ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
                
                val habitLogs by viewModel.allHabitLogs.collectAsStateWithLifecycle()
                FinancialHabitsTracker(viewModel = viewModel, habitLogs = habitLogs)
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "${transactions.size} total",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No transactions logged yet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap the + button to record your first income or expense.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction, currency = selectedCurrency)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select App Currency") },
            text = {
                Column {
                    AVAILABLE_CURRENCIES.forEach { curr ->
                        Surface(
                            onClick = {
                                viewModel.setCurrency(curr)
                                showCurrencyDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (curr.code == selectedCurrency.code) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = curr.name,
                                    fontWeight = if (curr.code == selectedCurrency.code) FontWeight.Bold else FontWeight.Normal,
                                    color = if (curr.code == selectedCurrency.code) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = curr.symbol,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (curr.code == selectedCurrency.code) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    currency: CurrencyInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "TOTAL BALANCE", 
                    style = MaterialTheme.typography.labelMedium, 
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    letterSpacing = 1.sp
                )
                Surface(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                ) {
                    Text(
                        currency.code, 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            
            Text(
                currency.format(balance), 
                style = MaterialTheme.typography.headlineLarge, 
                fontWeight = FontWeight.Bold, 
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text("INCOME", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(currency.format(income), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text("EXPENSES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(currency.format(expense), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionWithCategory,
    currency: CurrencyInfo
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    val bgColor = if (transaction.isIncome) IncomeIconBg else ExpenseIconBg
    val fgColor = if (transaction.isIncome) IncomeIconFg else ExpenseIconFg
    val amountColor = if (transaction.isIncome) IncomeGreen else ExpenseRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.categoryName.take(1).uppercase(),
                    color = fgColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.categoryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                val subtitle = if (transaction.note.isNotBlank()) "${transaction.note} • ${dateFormat.format(Date(transaction.timestamp))}" else dateFormat.format(Date(transaction.timestamp))
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "${if (transaction.isIncome) "+" else "-"}${currency.format(transaction.amount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
fun GamificationBanner(transactions: List<TransactionWithCategory>) {
    val expenses = transactions.filter { !it.isIncome }
    val sortedExpenseDates = expenses.map { it.timestamp }.sortedDescending()

    val streakDays = remember(sortedExpenseDates, transactions.size) {
        if (sortedExpenseDates.isEmpty()) {
            if (transactions.isNotEmpty()) 1 else 0
        } else {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val latestExpense = Calendar.getInstance().apply {
                timeInMillis = sortedExpenseDates.first()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val diff = today - latestExpense
            if (diff <= 0) 0 else (diff / (1000 * 60 * 60 * 24)).toInt()
        }
    }

    val (emoji, title, subtitle, bgColor, contentColor) = when {
        streakDays == 0 -> listOf(
            "💸",
            "Every penny counts",
            "Track your expenses closely today.",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        streakDays in 1..2 -> listOf(
            "🌱",
            "$streakDays Day No-Spend Streak!",
            "Great start! Let's keep the momentum.",
            Color(0xFFE8DEF8),
            Color(0xFF1D192B)
        )
        streakDays in 3..6 -> listOf(
            "🔥",
            "$streakDays Day No-Spend Streak!",
            "You are on fire! Amazing self-control.",
            Color(0xFFFFD8E4),
            Color(0xFF31111D)
        )
        else -> listOf(
            "💎",
            "$streakDays Day No-Spend Streak!",
            "Legendary savings! You're mastering your budget.",
            Color(0xFFC4EED0),
            Color(0xFF072711)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor as Color)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji as String, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title as String,
                    fontWeight = FontWeight.Bold,
                    color = contentColor as Color,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle as String,
                    style = MaterialTheme.typography.bodyMedium,
                    color = (contentColor as Color).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun FinancialHabitsTracker(
    viewModel: com.example.ui.BudgetViewModel,
    habitLogs: List<com.example.data.HabitLogEntity>
) {
    val habits = listOf(
        "Brought lunch from home" to "🍱",
        "Cooked dinner" to "🍳",
        "Walked/Biked to work" to "🚶",
        "Skipped coffee shop" to "☕",
        "Read personal finance" to "📚"
    )

    val todayDate = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date())
    }

    val todayLogs = habitLogs.filter { it.date == todayDate }.map { it.habitName }
    val isAllDone = todayLogs.size == habits.size

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Daily Financial Habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isAllDone) {
                    Text("🌟 Perfect Day!", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            habits.forEach { (habitName, emoji) ->
                val isChecked = todayLogs.contains(habitName)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleHabit(habitName, todayDate, !isChecked) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = habitName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleHabit(habitName, todayDate, it) }
                    )
                }
            }
        }
    }
}
