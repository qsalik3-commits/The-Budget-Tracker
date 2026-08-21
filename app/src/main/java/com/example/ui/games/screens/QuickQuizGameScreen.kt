package com.example.ui.games.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
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
import kotlinx.coroutines.delay

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

val QUIZ_BANK = listOf(
    QuizQuestion(
        question = "What does the 50/30/20 budgeting rule recommend for savings and debt repayment?",
        options = listOf("50%", "30%", "20%", "10%"),
        correctIndex = 2,
        explanation = "50% goes to Needs, 30% to Wants, and at least 20% to Savings & Investments."
    ),
    QuizQuestion(
        question = "How many months of basic living expenses are typically recommended for an emergency fund?",
        options = listOf("1 week", "3 to 6 months", "2 years", "10 years"),
        correctIndex = 1,
        explanation = "3 to 6 months of expenses protects you against sudden job loss or medical surprises."
    ),
    QuizQuestion(
        question = "What is compound interest?",
        options = listOf(
            "Interest paid only on original principal",
            "Interest earned on both principal and accumulated interest",
            "A penalty fee charged by banks",
            "A tax on checking accounts"
        ),
        correctIndex = 1,
        explanation = "Compound interest means your interest earns its own interest over time, accelerating growth!"
    ),
    QuizQuestion(
        question = "Which of the following is considered a 'Need' rather than a 'Want'?",
        options = listOf("Designer Sunglasses", "Streaming Subscription", "Basic Nutritious Groceries", "Video Game Console"),
        correctIndex = 2,
        explanation = "Basic groceries are essential for survival, while the others are enjoyable discretionary wants."
    ),
    QuizQuestion(
        question = "What happens to purchasing power during high inflation?",
        options = listOf(
            "Money buys more goods",
            "Money buys fewer goods over time",
            "Interest rates permanently drop to 0%",
            "Currency value triples"
        ),
        correctIndex = 1,
        explanation = "Inflation increases the general prices of goods, eroding what one dollar can purchase."
    ),
    QuizQuestion(
        question = "What is the primary purpose of a credit score?",
        options = listOf(
            "To measure how wealthy someone is",
            "To measure a borrower's likelihood of repaying debt",
            "To calculate annual taxes owed",
            "To qualify for government grants"
        ),
        correctIndex = 1,
        explanation = "Lenders use credit scores to evaluate risk when extending loans or credit cards."
    ),
    QuizQuestion(
        question = "What does 'Pay Yourself First' mean in personal finance?",
        options = listOf(
            "Buy yourself a luxury gift on payday",
            "Automatically move money to savings before spending on bills and wants",
            "Work overtime every weekend",
            "Borrow money from friends"
        ),
        correctIndex = 1,
        explanation = "Paying yourself first guarantees your savings goals are funded before lifestyle spending happens."
    ),
    QuizQuestion(
        question = "What is an index fund?",
        options = listOf(
            "A single high-risk startup company",
            "An investment fund tracking a broad market basket like the S&P 500",
            "A cryptocurrency wallet",
            "A lottery ticket pool"
        ),
        correctIndex = 1,
        explanation = "Index funds provide instant diversification across hundreds of companies at very low fees."
    ),
    QuizQuestion(
        question = "What is the Rule of 72 used for?",
        options = listOf(
            "Estimating years needed to double an investment based on rate of return",
            "Calculating annual car depreciation",
            "Determining your retirement age",
            "Budgeting grocery expenses"
        ),
        correctIndex = 0,
        explanation = "Divide 72 by your annual interest rate to estimate how many years it takes your money to double."
    ),
    QuizQuestion(
        question = "Why is carrying a high credit card balance month-to-month risky?",
        options = listOf(
            "High compound interest charges create an expensive debt spiral",
            "The bank closes your account immediately",
            "Credit card rewards double",
            "You cannot use debit cards anymore"
        ),
        correctIndex = 0,
        explanation = "Credit cards typically carry 20-30% APR, making carried balances extremely costly."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickQuizGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(60) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    var questionList by remember { mutableStateOf(QUIZ_BANK.shuffled()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(1) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isLastCorrect by remember { mutableStateOf(true) }

    // 60-second Timer Loop
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        } else if (isRunning && timeLeft <= 0) {
            isRunning = false
            isFinished = true
            val finalXp = (score * 0.75).toInt()
            val finalCoins = score
            viewModel.recordGameFinished("quick_quiz", score, finalXp, finalCoins)
        }
    }

    val currentQuestion = questionList[currentIndex % questionList.size]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ 60-Second Money Quiz", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Stats: Timer & Score & Combo
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Timer,
                            contentDescription = "Timer",
                            tint = if (timeLeft <= 10 && isRunning) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${timeLeft}s",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (timeLeft <= 10 && isRunning) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Combo Badge
                    Surface(
                        color = if (combo > 1) Color(0xFFFF6F00) else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🔥 ${combo}x Combo",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Score
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Score", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$score pts", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF6750A4))
                    }
                }
            }

            if (!isRunning && !isFinished) {
                // Intro Screen
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("⚡", fontSize = 48.sp)
                        Text(
                            "Fast Financial Knowledge",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Answer as many personal finance questions as you can in 60 seconds. Build streaks for multiplier combo points!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                timeLeft = 60
                                score = 0
                                combo = 1
                                correctCount = 0
                                wrongCount = 0
                                currentIndex = 0
                                questionList = QUIZ_BANK.shuffled()
                                isRunning = true
                                isFinished = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start 60-Second Challenge ➔", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            } else if (isRunning) {
                // Active Quiz Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Q${currentIndex + 1}: ${currentQuestion.question}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            lineHeight = 22.sp
                        )

                        // 4 Option Buttons
                        currentQuestion.options.forEachIndexed { optIndex, optionText ->
                            OutlinedButton(
                                onClick = {
                                    val isCorrect = optIndex == currentQuestion.correctIndex
                                    isLastCorrect = isCorrect
                                    if (isCorrect) {
                                        val pointsEarned = 10 * combo
                                        score += pointsEarned
                                        combo = (combo + 1).coerceAtMost(5)
                                        correctCount += 1
                                        feedbackMessage = "✅ Correct! +$pointsEarned pts"
                                    } else {
                                        combo = 1
                                        wrongCount += 1
                                        feedbackMessage = "❌ Not quite: ${currentQuestion.explanation}"
                                    }
                                    currentIndex += 1
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = optionText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Short live feedback toast
                        if (feedbackMessage != null) {
                            Text(
                                text = feedbackMessage!!,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isLastCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                // Final Results Screen
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("🏆 TIME UP! 🏆", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF4A148C))
                        Text(
                            text = "Final Score: $score pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF6750A4)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅ Correct: $correctCount", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("❌ Wrong: $wrongCount", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        }

                        Surface(
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "+${(score * 0.75).toInt()} XP  |  +$score Virtual Coins",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261800),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Button(
                            onClick = {
                                timeLeft = 60
                                score = 0
                                combo = 1
                                correctCount = 0
                                wrongCount = 0
                                currentIndex = 0
                                questionList = QUIZ_BANK.shuffled()
                                isRunning = true
                                isFinished = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play Again ⚡", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
