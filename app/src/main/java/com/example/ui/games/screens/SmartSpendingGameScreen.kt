package com.example.ui.games.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.games.MoneyGamesViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

data class SpendingScenario(
    val id: String,
    val title: String,
    val difficulty: String,
    val monthlyIncome: Int,
    val backstory: String,
    val targetSavingsMin: Int,
    val targetNeedsMax: Int,
    val targetWantsMax: Int
)

val SPENDING_SCENARIOS = listOf(
    SpendingScenario(
        id = "easy_1",
        title = "First Apartment & Job",
        difficulty = "Easy",
        monthlyIncome = 3000,
        backstory = "Jordan just started their first entry-level career earning $3,000/mo. Help them build a sustainable monthly budget!",
        targetSavingsMin = 600,  // 20%
        targetNeedsMax = 1500,   // 50%
        targetWantsMax = 900     // 30%
    ),
    SpendingScenario(
        id = "med_1",
        title = "Mid-Career Family",
        difficulty = "Medium",
        monthlyIncome = 5500,
        backstory = "Taylor has a family of 3 with variable utility bills and childcare costs. Balance essential needs with a solid emergency fund.",
        targetSavingsMin = 1100, // 20%
        targetNeedsMax = 2750,   // 50%
        targetWantsMax = 1650    // 30%
    ),
    SpendingScenario(
        id = "hard_1",
        title = "Freelance & Debt Paydown",
        difficulty = "Hard",
        monthlyIncome = 7000,
        backstory = "Sam makes $7,000/mo freelancing but has irregular tax payments and student loans. Maximize savings and guard against high fixed costs.",
        targetSavingsMin = 1750, // 25%
        targetNeedsMax = 3500,   // 50%
        targetWantsMax = 1750    // 25%
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSpendingGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    var selectedScenarioIndex by remember { mutableStateOf(0) }
    val scenario = SPENDING_SCENARIOS[selectedScenarioIndex]

    // Allocation States
    var housing by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.35f) }
    var groceries by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.15f) }
    var utilities by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.08f) }
    var entertainment by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.15f) }
    var savings by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.15f) }
    var emergencyFund by remember(selectedScenarioIndex) { mutableStateOf(scenario.monthlyIncome * 0.12f) }

    var evaluated by remember(selectedScenarioIndex) { mutableStateOf(false) }
    var score by remember(selectedScenarioIndex) { mutableStateOf(0) }
    var feedbackList by remember(selectedScenarioIndex) { mutableStateOf(listOf<String>()) }

    val totalNeeds = housing + groceries + utilities
    val totalWants = entertainment
    val totalSavings = savings + emergencyFund
    val totalAllocated = totalNeeds + totalWants + totalSavings
    val remaining = scenario.monthlyIncome - totalAllocated

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧠 Smart Spending", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        housing = scenario.monthlyIncome * 0.35f
                        groceries = scenario.monthlyIncome * 0.15f
                        utilities = scenario.monthlyIncome * 0.08f
                        entertainment = scenario.monthlyIncome * 0.15f
                        savings = scenario.monthlyIncome * 0.15f
                        emergencyFund = scenario.monthlyIncome * 0.12f
                        evaluated = false
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset sliders")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // Difficulty / Scenario Selector Tabs
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SPENDING_SCENARIOS.forEachIndexed { index, s ->
                    SegmentedButton(
                        selected = selectedScenarioIndex == index,
                        onClick = { selectedScenarioIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = SPENDING_SCENARIOS.size)
                    ) {
                        Text(s.difficulty)
                    }
                }
            }

            // Scenario Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(scenario.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Income: $${scenario.monthlyIncome}/mo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(scenario.backstory, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Budget Remaining Status Indicator
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (abs(remaining) < 5) Color(0xFFE8F5E9) else if (remaining < 0) Color(0xFFFFEBEE) else Color(0xFFFFF8E1)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (abs(remaining) < 5) Color(0xFF4CAF50) else if (remaining < 0) Color(0xFFE57373) else Color(0xFFFFB74D)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (remaining < 0) "Over Budget by $${abs(remaining.roundToInt())}" else "Unallocated: $${remaining.roundToInt()}",
                            fontWeight = FontWeight.Bold,
                            color = if (remaining < 0) Color(0xFFC62828) else if (abs(remaining) < 5) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                        Text(
                            text = "Target: 50% Needs, 30% Wants, 20%+ Savings",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${((totalAllocated / scenario.monthlyIncome) * 100).roundToInt()}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }

            // Category Sliders
            Text("Allocate Categories", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

            AllocationSlider(label = "🏠 Rent & Housing", amount = housing, max = scenario.monthlyIncome.toFloat()) { housing = it }
            AllocationSlider(label = "🛒 Groceries & Food", amount = groceries, max = scenario.monthlyIncome.toFloat()) { groceries = it }
            AllocationSlider(label = "💡 Utilities & Bills", amount = utilities, max = scenario.monthlyIncome.toFloat()) { utilities = it }
            AllocationSlider(label = "🍿 Entertainment & Dining", amount = entertainment, max = scenario.monthlyIncome.toFloat()) { entertainment = it }
            AllocationSlider(label = "💰 Long-term Savings", amount = savings, max = scenario.monthlyIncome.toFloat()) { savings = it }
            AllocationSlider(label = "🛡️ Emergency Fund", amount = emergencyFund, max = scenario.monthlyIncome.toFloat()) { emergencyFund = it }

            // Submit / Evaluate Button
            Button(
                onClick = {
                    val feedbacks = mutableListOf<String>()
                    var pts = 100

                    if (remaining < 0) {
                        pts -= 30
                        feedbacks.add("⚠️ You are spending more than your monthly income! Reduce discretionary spending.")
                    } else if (remaining > (scenario.monthlyIncome * 0.15f)) {
                        pts -= 15
                        feedbacks.add("💡 You left more than 15% unallocated. Put extra funds into high-yield savings or investing.")
                    }

                    val needsPercent = (totalNeeds / scenario.monthlyIncome) * 100
                    if (needsPercent > 55) {
                        pts -= 20
                        feedbacks.add("⚠️ Fixed needs (${needsPercent.roundToInt()}%) exceed 50% of income. Try downsizing housing or shopping smarter.")
                    } else {
                        feedbacks.add("✅ Great job keeping essential living expenses at ${needsPercent.roundToInt()}%!")
                    }

                    val savingsPercent = (totalSavings / scenario.monthlyIncome) * 100
                    if (savingsPercent < 20) {
                        pts -= 25
                        feedbacks.add("⚠️ Savings (${savingsPercent.roundToInt()}%) is below the recommended 20% target.")
                    } else {
                        feedbacks.add("🌟 Excellent savings rate of ${savingsPercent.roundToInt()}% for building wealth!")
                    }

                    val wantsPercent = (totalWants / scenario.monthlyIncome) * 100
                    if (wantsPercent > 35) {
                        pts -= 15
                        feedbacks.add("ℹ️ Discretionary wants (${wantsPercent.roundToInt()}%) slightly high, leaving less for goals.")
                    }

                    score = pts.coerceIn(10, 100)
                    feedbackList = feedbacks
                    evaluated = true

                    val xpReward = (score * 0.6).roundToInt()
                    val coinReward = (score * 0.8).roundToInt()
                    viewModel.recordGameFinished("smart_spending", score, xpReward, coinReward)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Evaluate My Budget", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // Results and Financial Coaching Breakdown
            if (evaluated) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (score >= 80) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (score >= 80) Color(0xFF81C784) else Color(0xFFFFB74D))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Evaluation Results", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Score: $score / 100",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = if (score >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                        }

                        Divider()

                        feedbackList.forEach { f ->
                            Text(text = f, style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Earned: +${(score * 0.6).roundToInt()} XP | +${(score * 0.8).roundToInt()} Coins",
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
}

@Composable
fun AllocationSlider(
    label: String,
    amount: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(
                    "$${amount.roundToInt()} (${((amount / max) * 100).roundToInt()}%)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = amount,
                onValueChange = onValueChange,
                valueRange = 0f..max,
                steps = 40
            )
        }
    }
}
