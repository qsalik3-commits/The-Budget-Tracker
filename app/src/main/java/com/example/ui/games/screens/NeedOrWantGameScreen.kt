package com.example.ui.games.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class NeedWantItem(
    val title: String,
    val icon: String,
    val costExample: String,
    val isNeed: Boolean,
    val category: String,
    val explanation: String
)

val NEED_WANT_ITEMS = listOf(
    NeedWantItem(
        title = "Nutritious Groceries",
        icon = "🥦",
        costExample = "$80 / week",
        isNeed = true,
        category = "Food",
        explanation = "Basic nutritional sustenance is a biological necessity for physical health and daily energy."
    ),
    NeedWantItem(
        title = "Designer Sneakers",
        icon = "👟",
        costExample = "$220",
        isNeed = false,
        category = "Fashion",
        explanation = "While having protective footwear is a need, premium brand designer sneakers are a luxury want that you can budget for."
    ),
    NeedWantItem(
        title = "Prescription Medications",
        icon = "💊",
        costExample = "$35 / month",
        isNeed = true,
        category = "Healthcare",
        explanation = "Essential health and prescribed medicine are non-negotiable needs."
    ),
    NeedWantItem(
        title = "4K Video Game Console",
        icon = "🎮",
        costExample = "$500",
        isNeed = false,
        category = "Entertainment",
        explanation = "Gaming consoles are fun recreational wants. Planning ahead in your 30% wants budget makes buying guilt-free!"
    ),
    NeedWantItem(
        title = "Public Transit / Commute Pass",
        icon = "🚇",
        costExample = "$75 / month",
        isNeed = true,
        category = "Transportation",
        explanation = "Reliable transit to get to work or school is a foundational need for earning an income."
    ),
    NeedWantItem(
        title = "Daily $7 Artisan Latte",
        icon = "☕",
        costExample = "$140 / month",
        isNeed = false,
        category = "Beverages",
        explanation = "Caffeine or breakfast at home is cheap; gourmet coffee shop drinks are a lifestyle want."
    ),
    NeedWantItem(
        title = "Electricity & Heating Utilities",
        icon = "💡",
        costExample = "$110 / month",
        isNeed = true,
        category = "Utilities",
        explanation = "Heating, water, and basic electricity are core shelter necessities."
    ),
    NeedWantItem(
        title = "First-Class Airline Upgrade",
        icon = "✈️",
        costExample = "$450 upgrade fee",
        isNeed = false,
        category = "Travel",
        explanation = "Reaching your destination safely is the requirement; extra luxury seating is a comfort want."
    ),
    NeedWantItem(
        title = "Basic Winter Coat in Freezing Climate",
        icon = "🧥",
        costExample = "$90",
        isNeed = true,
        category = "Clothing",
        explanation = "Appropriate weather gear is a physical protection need during harsh winter conditions."
    ),
    NeedWantItem(
        title = "5 Different Streaming TV Subscriptions",
        icon = "📺",
        costExample = "$65 / month",
        isNeed = false,
        category = "Media",
        explanation = "Entertainment is great for unwinding, but multiple paid streaming services are discretionary wants."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedOrWantGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    var items by remember { mutableStateOf(NEED_WANT_ITEMS.shuffled()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(1) }
    var maxCombo by remember { mutableStateOf(1) }
    var lastAnswerFeedback by remember { mutableStateOf<NeedWantFeedback?>(null) }
    var isFinished by remember { mutableStateOf(false) }

    val currentItem = if (currentIndex < items.size) items[currentIndex] else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Need or Want?", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header
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
                    Text(
                        text = "Item ${currentIndex.coerceAtMost(items.size)} of ${items.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Surface(
                        color = if (combo > 1) Color(0xFFFF6F00) else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🔥 ${combo}x Multiplier",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "$score pts",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (!isFinished && currentItem != null) {
                // Item Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(currentItem.icon, fontSize = 60.sp)

                        Text(
                            text = currentItem.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Typical Cost: ${currentItem.costExample} • ${currentItem.category}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Large Choice Buttons: NEED vs WANT
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Button(
                                onClick = {
                                    val correct = currentItem.isNeed
                                    processAnswer(correct, currentItem) { pts, newCombo, fb ->
                                        score += pts
                                        combo = newCombo
                                        if (combo > maxCombo) maxCombo = combo
                                        lastAnswerFeedback = fb
                                        if (currentIndex + 1 >= items.size) {
                                            isFinished = true
                                            val finalXp = (score * 0.8).toInt()
                                            val finalCoins = score
                                            viewModel.recordGameFinished("need_want", score, finalXp, finalCoins)
                                        } else {
                                            currentIndex += 1
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("🛡️ NEED", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }

                            Button(
                                onClick = {
                                    val correct = !currentItem.isNeed
                                    processAnswer(correct, currentItem) { pts, newCombo, fb ->
                                        score += pts
                                        combo = newCombo
                                        if (combo > maxCombo) maxCombo = combo
                                        lastAnswerFeedback = fb
                                        if (currentIndex + 1 >= items.size) {
                                            isFinished = true
                                            val finalXp = (score * 0.8).toInt()
                                            val finalCoins = score
                                            viewModel.recordGameFinished("need_want", score, finalXp, finalCoins)
                                        } else {
                                            currentIndex += 1
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                            ) {
                                Text("✨ WANT", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Educational Feedback Toast / Card
                lastAnswerFeedback?.let { fb ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (fb.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (fb.isCorrect) Color(0xFF81C784) else Color(0xFFFFB74D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (fb.isCorrect) "✅ Correct! (${if (fb.item.isNeed) "Essential Need" else "Reasonable Want"})" else "💡 Learning Insight",
                                fontWeight = FontWeight.Bold,
                                color = if (fb.isCorrect) Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontSize = 14.sp
                            )
                            Text(fb.item.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                // Game Finished Summary
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
                        Text("🎉 ROUND COMPLETE! 🎉", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF4A148C))
                        Text(
                            text = "Total Score: $score pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF6750A4)
                        )
                        Text(
                            text = "Max Combo: ${maxCombo}x Multiplier",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF6F00)
                        )

                        Surface(
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "+${(score * 0.8).toInt()} XP  |  +$score Virtual Coins",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261800),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Button(
                            onClick = {
                                items = NEED_WANT_ITEMS.shuffled()
                                currentIndex = 0
                                score = 0
                                combo = 1
                                maxCombo = 1
                                lastAnswerFeedback = null
                                isFinished = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play Again 🛒", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class NeedWantFeedback(
    val isCorrect: Boolean,
    val item: NeedWantItem
)

private fun processAnswer(
    correct: Boolean,
    item: NeedWantItem,
    onComplete: (points: Int, newCombo: Int, feedback: NeedWantFeedback) -> Unit
) {
    val pts = if (correct) 15 else 5
    val newCombo = if (correct) 2 else 1
    onComplete(pts, newCombo, NeedWantFeedback(correct, item))
}
