package com.example.ui.games.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.games.MoneyGamesViewModel

data class SaveGoalChallenge(
    val id: String,
    val title: String,
    val targetAmount: Int,
    val totalDays: Int,
    val dailyIncome: Int,
    val dailyFixedExpenses: Int,
    val description: String
)

data class DayDilemma(
    val story: String,
    val optionA: String,
    val costA: Int,
    val optionB: String,
    val costB: Int,
    val lesson: String
)

val GOAL_CHALLENGES = listOf(
    SaveGoalChallenge(
        id = "goal_1",
        title = "🛡️ Emergency Buffer",
        targetAmount = 500,
        totalDays = 10,
        dailyIncome = 120,
        dailyFixedExpenses = 60,
        description = "Save $500 in 10 days to protect against unexpected life emergencies."
    ),
    SaveGoalChallenge(
        id = "goal_2",
        title = "💻 Work Laptop Fund",
        targetAmount = 1000,
        totalDays = 14,
        dailyIncome = 160,
        dailyFixedExpenses = 75,
        description = "Build a $1,000 upgrade fund in 14 days while handling real-life temptations."
    ),
    SaveGoalChallenge(
        id = "goal_3",
        title = "✈️ Dream Trip Fund",
        targetAmount = 1800,
        totalDays = 20,
        dailyIncome = 200,
        dailyFixedExpenses = 90,
        description = "Save $1,800 in 20 days for a memorable bucket-list travel experience."
    )
)

val DAILY_DILEMMAS = listOf(
    DayDilemma(
        story = "Friends invited you to an upscale sushi dinner.",
        optionA = "Attend & split the expensive bill (-$45)",
        costA = 45,
        optionB = "Join for tea only / suggest picnic (-$10)",
        costB = 10,
        lesson = "Socializing doesn't have to drain your savings if you set intentional boundaries."
    ),
    DayDilemma(
        story = "Flash sale online on trending noise-canceling headphones (50% off).",
        optionA = "Buy now to 'save' money on the discount (-$60)",
        costA = 60,
        optionB = "Pass; my current pair still works well (-$0)",
        costB = 0,
        lesson = "A discount is only saving money if you were 100% planning to buy it anyway."
    ),
    DayDilemma(
        story = "A neighbor offers $40 to help them assemble flat-pack furniture.",
        optionA = "Take the side-gig opportunity (+$40)",
        costA = -40,
        optionB = "Relax at home for the afternoon ($0)",
        costB = 0,
        lesson = "Earning extra income accelerates your financial goals much faster than extreme cutting."
    ),
    DayDilemma(
        story = "Your car tire has a slow leak and needs a patch.",
        optionA = "Repair it immediately at local garage (-$30)",
        costA = 30,
        optionB = "Ignore it and hope it holds up (-$0 now, risk -$150 later)",
        costB = 0,
        lesson = "Preventative maintenance always saves money compared to emergency repairs."
    ),
    DayDilemma(
        story = "You forgot lunch at home before going to work.",
        optionA = "Order delivery with service & tip fees (-$25)",
        costA = 25,
        optionB = "Grab a healthy sandwich at the grocery store (-$7)",
        costB = 7,
        lesson = "Convenience fees and delivery apps add up quietly over time."
    ),
    DayDilemma(
        story = "Unexpected tax refund arrived in the mail!",
        optionA = "Deposit 100% straight into savings fund (+$60)",
        costA = -60,
        optionB = "Splurge on designer clothes (-$50)",
        costB = 50,
        lesson = "Treating windfalls as savings fuel creates massive financial momentum."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveTheGoalGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    var selectedChallengeIndex by remember { mutableStateOf(0) }
    val challenge = GOAL_CHALLENGES[selectedChallengeIndex]

    var currentDay by remember(selectedChallengeIndex) { mutableStateOf(1) }
    var currentSaved by remember(selectedChallengeIndex) { mutableStateOf(0) }
    var gameCompleted by remember(selectedChallengeIndex) { mutableStateOf(false) }
    var gameWon by remember(selectedChallengeIndex) { mutableStateOf(false) }
    var logHistory by remember(selectedChallengeIndex) { mutableStateOf(listOf<String>()) }
    var currentDilemma by remember(selectedChallengeIndex, currentDay) {
        mutableStateOf(DAILY_DILEMMAS[(currentDay - 1) % DAILY_DILEMMAS.size])
    }

    val progress = (currentSaved.toFloat() / challenge.targetAmount).coerceIn(0f, 1f)
    val daysRemaining = (challenge.totalDays - currentDay + 1).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💰 Save the Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        currentDay = 1
                        currentSaved = 0
                        gameCompleted = false
                        gameWon = false
                        logHistory = emptyList()
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Restart Challenge")
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
            // Target Goal Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                GOAL_CHALLENGES.forEachIndexed { index, c ->
                    SegmentedButton(
                        selected = selectedChallengeIndex == index,
                        onClick = { selectedChallengeIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = GOAL_CHALLENGES.size)
                    ) {
                        Text(c.title.substringBefore(" ").ifBlank { "Goal ${index + 1}" })
                    }
                }
            }

            // Challenge Banner & Progress Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(challenge.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(challenge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Stats Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Current Saved", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$$currentSaved", fontWeight = FontWeight.Black, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Target Goal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${challenge.targetAmount}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF2E7D32),
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Day $currentDay of ${challenge.totalDays}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("$daysRemaining Days Left", fontWeight = FontWeight.Bold, color = if (daysRemaining <= 3) Color(0xFFC62828) else MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    }
                }
            }

            // Active Game Dilemma or Completion Screen
            if (!gameCompleted) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "📅 Day $currentDay Event",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Text(
                            text = currentDilemma.story,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Daily Base Savings: +$${challenge.dailyIncome - challenge.dailyFixedExpenses} (Income: $${challenge.dailyIncome}, Fixed Bills: $${challenge.dailyFixedExpenses})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Option A
                        OutlinedButton(
                            onClick = {
                                handleDayChoice(
                                    challenge = challenge,
                                    choiceCost = currentDilemma.costA,
                                    currentSaved = currentSaved,
                                    currentDay = currentDay,
                                    onUpdate = { newSaved, newDay, completed, won, log ->
                                        currentSaved = newSaved
                                        currentDay = newDay
                                        gameCompleted = completed
                                        gameWon = won
                                        logHistory = logHistory + log
                                        if (completed && won) {
                                            viewModel.recordGameFinished("save_the_goal", 100, 75, 120)
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(currentDilemma.optionA, textAlign = TextAlign.Center)
                        }

                        // Option B
                        Button(
                            onClick = {
                                handleDayChoice(
                                    challenge = challenge,
                                    choiceCost = currentDilemma.costB,
                                    currentSaved = currentSaved,
                                    currentDay = currentDay,
                                    onUpdate = { newSaved, newDay, completed, won, log ->
                                        currentSaved = newSaved
                                        currentDay = newDay
                                        gameCompleted = completed
                                        gameWon = won
                                        logHistory = logHistory + log
                                        if (completed && won) {
                                            viewModel.recordGameFinished("save_the_goal", 100, 75, 120)
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(currentDilemma.optionB, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                // Game Finished Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (gameWon) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (gameWon) Color(0xFF4CAF50) else Color(0xFFE57373))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (gameWon) "🏆 GOAL ACHIEVED!" else "⏳ DEADLINE REACHED",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (gameWon) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Text(
                            text = if (gameWon)
                                "Outstanding job! You successfully saved $${currentSaved} and reached your target!"
                            else
                                "You reached Day ${challenge.totalDays} with $${currentSaved} saved. Try again with smarter daily choices!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (gameWon) {
                            Surface(
                                color = Color(0xFF6750A4),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "+75 XP  |  +120 Virtual Coins",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                currentDay = 1
                                currentSaved = 0
                                gameCompleted = false
                                gameWon = false
                                logHistory = emptyList()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play Again")
                        }
                    }
                }
            }

            // History Logs
            if (logHistory.isNotEmpty()) {
                Text("Decision Log", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                logHistory.takeLast(4).reversed().forEach { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun handleDayChoice(
    challenge: SaveGoalChallenge,
    choiceCost: Int,
    currentSaved: Int,
    currentDay: Int,
    onUpdate: (newSaved: Int, newDay: Int, completed: Boolean, won: Boolean, log: String) -> Unit
) {
    val dailyBase = challenge.dailyIncome - challenge.dailyFixedExpenses
    val netDaySavings = dailyBase - choiceCost
    val newSaved = (currentSaved + netDaySavings).coerceAtLeast(0)

    val log = "Day $currentDay: Saved +$$netDaySavings (Total: $$newSaved / $${challenge.targetAmount})"

    if (newSaved >= challenge.targetAmount) {
        onUpdate(newSaved, currentDay, true, true, log)
    } else if (currentDay >= challenge.totalDays) {
        onUpdate(newSaved, currentDay, true, false, log)
    } else {
        onUpdate(newSaved, currentDay + 1, false, false, log)
    }
}
