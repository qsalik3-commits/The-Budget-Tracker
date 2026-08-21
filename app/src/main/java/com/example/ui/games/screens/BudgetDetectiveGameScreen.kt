package com.example.ui.games.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.games.MoneyGamesViewModel

data class DetectiveCase(
    val id: String,
    val title: String,
    val suspectName: String,
    val monthlyIncome: Int,
    val monthlyExpense: Int,
    val backstory: String,
    val expenseBreakdown: List<Pair<String, Int>>,
    val clues: List<String>,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

val DETECTIVE_CASES = listOf(
    DetectiveCase(
        id = "case_1",
        title = "Case #1: The Phantom Subscriptions",
        suspectName = "Alex (Graphic Designer)",
        monthlyIncome = 3500,
        monthlyExpense = 3850,
        backstory = "Alex makes a great salary ($3,500/mo) but their account goes negative by $350 every single month. Inspect the breakdown and find the hidden financial leak.",
        expenseBreakdown = listOf(
            "Rent & Utilities" to 1400,
            "Groceries" to 450,
            "Transportation" to 300,
            "Streaming & App Subscriptions (14 apps)" to 480,
            "Daily Artisan Coffee & Snacks" to 350,
            "Gym & Wellness Passes (Unused)" to 250,
            "Dining Out & Socializing" to 620
        ),
        clues = listOf(
            "🔎 Clue 1: Alex signed up for 14 free trials over the past 2 years that converted to recurring paid charges.",
            "🔎 Clue 2: Alex hasn't visited the boutique gym in over 6 months.",
            "🔎 Clue 3: Fixed housing and essential groceries are completely reasonable (under 55% of income)."
        ),
        options = listOf(
            "Alex is paying too much rent for their apartment.",
            "Alex is spending $730/mo on unused subscriptions, gym memberships, and micro-apps.",
            "Alex is eating too many essential groceries at home.",
            "Alex's employer is deducting unauthorized payroll taxes."
        ),
        correctOptionIndex = 1,
        explanation = "Solved! The major leak was $730/month in zombie subscriptions and unused memberships. Canceling them instantly brings Alex from -$350 to +$380 positive cash flow!"
    ),
    DetectiveCase(
        id = "case_2",
        title = "Case #2: The Auto Loan Trap",
        suspectName = "Jordan (Sales Rep)",
        monthlyIncome = 4000,
        monthlyExpense = 4300,
        backstory = "Jordan wonders why they can never save for a house down payment despite making sales commission.",
        expenseBreakdown = listOf(
            "Apartment Rent" to 1200,
            "Luxury SUV Loan Payment" to 850,
            "Comprehensive Auto Insurance" to 380,
            "Gas & Vehicle Maintenance" to 280,
            "Groceries" to 400,
            "Savings" to 50,
            "Discretionary Spending" to 1140
        ),
        clues = listOf(
            "🔎 Clue 1: Total car costs (loan + insurance + gas) equal $1,510/mo — nearly 38% of Jordan's entire income!",
            "🔎 Clue 2: Financial guidelines recommend keeping total transportation under 15% of take-home pay."
        ),
        options = listOf(
            "Jordan should stop buying groceries altogether.",
            "Jordan is spending 38% of their income on a single luxury vehicle.",
            "Apartment rent of $1,200 is excessive for Jordan's income.",
            "Sales commission is not taxed properly."
        ),
        correctOptionIndex = 1,
        explanation = "Case Closed! The luxury SUV is costing $1,510/month in payments, insurance, and fuel. Swapping for a reliable economical car frees up over $900/month for long-term investments!"
    ),
    DetectiveCase(
        id = "case_3",
        title = "Case #3: The Minimum Payment Trap",
        suspectName = "Morgan (Freelance Writer)",
        monthlyIncome = 4500,
        monthlyExpense = 4600,
        backstory = "Morgan pays all credit card bills on time every month, yet their total balance keeps growing.",
        expenseBreakdown = listOf(
            "Rent & Bills" to 1600,
            "Credit Card Minimum Payments" to 220,
            "Credit Card Interest Accrued (26% APR)" to 380,
            "Food & Living" to 800,
            "Travel & Entertainment" to 1600
        ),
        clues = listOf(
            "🔎 Clue 1: Morgan only pays the $220 minimum due on a $15,000 balance at 26% APR.",
            "🔎 Clue 2: The monthly interest charge ($380) is higher than the minimum payment, causing compounding debt growth."
        ),
        options = listOf(
            "The bank made a software calculation error.",
            "Rent is too high for a freelancer.",
            "Paying only minimums on high-interest debt causes negative amortization where debt grows every month.",
            "Morgan should switch to cash-only without paying back the balance."
        ),
        correctOptionIndex = 2,
        explanation = "Genius deduction! Paying only the minimum on credit cards when interest exceeds the payment causes debt to spiral. Aggressive debt paydown or balance transfers stop the leak!"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetectiveGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    var selectedCaseIndex by remember { mutableStateOf(0) }
    val currentCase = DETECTIVE_CASES[selectedCaseIndex]

    var selectedAnswerIndex by remember(selectedCaseIndex) { mutableStateOf<Int?>(null) }
    var solved by remember(selectedCaseIndex) { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Budget Detective", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Case Selector Tabs
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                DETECTIVE_CASES.forEachIndexed { index, c ->
                    SegmentedButton(
                        selected = selectedCaseIndex == index,
                        onClick = { selectedCaseIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = DETECTIVE_CASES.size)
                    ) {
                        Text("Case ${index + 1}")
                    }
                }
            }

            // Case Dossier Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(currentCase.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Surface(
                            color = Color(0xFF6750A4),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Mystery File",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("Suspect: ${currentCase.suspectName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(currentCase.backstory, style = MaterialTheme.typography.bodyMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Income: $${currentCase.monthlyIncome}/mo", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("Expenses: $${currentCase.monthlyExpense}/mo", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Evidence Breakdown
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📋 Bank Statement Evidence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    currentCase.expenseBreakdown.forEach { (category, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text("$$amount", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    }
                }
            }

            // Clues Dossier
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFE082))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🕵️ Detective Notes & Clues", fontWeight = FontWeight.Bold, color = Color(0xFF8D6E63))
                    currentCase.clues.forEach { clue ->
                        Text(clue, fontSize = 12.sp, color = Color(0xFF4E342E), lineHeight = 16.sp)
                    }
                }
            }

            // Hypothesis Options
            Text("Identify the Biggest Financial Problem:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

            currentCase.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswerIndex == index
                val isCorrect = index == currentCase.correctOptionIndex
                val buttonColor = if (solved) {
                    if (isCorrect) Color(0xFFE8F5E9) else if (isSelected) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
                } else {
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = buttonColor),
                    border = BorderStroke(
                        1.dp,
                        if (solved && isCorrect) Color(0xFF4CAF50) else if (solved && isSelected) Color(0xFFE57373) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    onClick = {
                        if (!solved) {
                            selectedAnswerIndex = index
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = selectedAnswerIndex == index,
                            onClick = { if (!solved) selectedAnswerIndex = index }
                        )
                        Text(text = option, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Solve / Check Button
            if (!solved) {
                Button(
                    onClick = {
                        if (selectedAnswerIndex != null) {
                            solved = true
                            val correct = selectedAnswerIndex == currentCase.correctOptionIndex
                            val xp = if (correct) 80 else 20
                            val coins = if (correct) 120 else 30
                            viewModel.recordGameFinished("budget_detective", if (correct) 100 else 40, xp, coins)
                        }
                    },
                    enabled = selectedAnswerIndex != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit Verdict", fontWeight = FontWeight.Bold)
                }
            } else {
                // Explanation & Rewards
                val isCorrect = selectedAnswerIndex == currentCase.correctOptionIndex
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE57373))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isCorrect) "🔍 CASE SOLVED!" else "❌ MISSED THE CULPRIT",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Text(currentCase.explanation, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (isCorrect) "+80 XP | +120 Virtual Coins" else "+20 XP | +30 Virtual Coins",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
