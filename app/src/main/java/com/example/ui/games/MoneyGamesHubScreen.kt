package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.games.components.*
import com.example.ui.games.data.DailyChallenge
import com.example.ui.games.data.GameConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyGamesHubScreen(
    viewModel: MoneyGamesViewModel,
    onNavigateToGame: (String) -> Unit
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val rewardPopup by viewModel.rewardPopup.collectAsStateWithLifecycle()

    var showAchievementsSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🎮 Money Games",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Play. Learn. Level Up.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAchievementsSheet = true }) {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = Color(0xFFFFB300)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Stats Header: Streak, XP, Level, Virtual Coins
            GameHeaderStats(
                profile = gameState,
                onOpenAchievements = { showAchievementsSheet = true }
            )

            // Daily Challenge Card
            DailyChallengeSection(
                viewModel = viewModel,
                completedChallenges = gameState.completedDailyChallenges
            )

            // Section Title: Featured Hero Game
            Text(
                text = "Featured Hero Game",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Game 6 — Hero: 🏙️ BUILD YOUR MONEY CITY
            HeroMoneyCityCard(
                profile = gameState,
                onClick = { onNavigateToGame("game_money_city") }
            )

            // Section Title: Interactive Mini-Games
            Text(
                text = "Interactive Mini-Games",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Game 1 — 🧠 Smart Spending
            StandardGameCard(
                title = "Smart Spending",
                tagline = "Allocate income across housing, food & savings to build a balanced budget.",
                icon = "🧠",
                badgeText = "Budgeting",
                badgeColor = Color(0xFF6750A4),
                onClick = { onNavigateToGame("game_smart_spending") }
            )

            // Game 2 — 💰 Save the Goal
            StandardGameCard(
                title = "Save the Goal",
                tagline = "Overcome unexpected life temptations and hit your savings target before time expires.",
                icon = "💰",
                badgeText = "Challenge",
                badgeColor = Color(0xFF2E7D32),
                onClick = { onNavigateToGame("game_save_goal") }
            )

            // Game 3 — 📊 Budget Detective
            StandardGameCard(
                title = "Budget Detective",
                tagline = "Investigate financial mysteries, inspect bank statements, and find hidden leaks.",
                icon = "📊",
                badgeText = "Mystery",
                badgeColor = Color(0xFFE65100),
                onClick = { onNavigateToGame("game_budget_detective") }
            )

            // Game 4 — ⚡ 60-Second Money Quiz
            StandardGameCard(
                title = "60-Second Money Quiz",
                tagline = "Fast financial trivia! Test your saving, investing & budgeting knowledge on the clock.",
                icon = "⚡",
                badgeText = "Fast Trivia",
                badgeColor = Color(0xFFD81B60),
                onClick = { onNavigateToGame("game_quick_quiz") }
            )

            // Game 5 — 🛒 Need or Want?
            StandardGameCard(
                title = "Need or Want?",
                tagline = "Swipe and categorize expenses with constructive, non-shaming budgeting insights.",
                icon = "🛒",
                badgeText = "Quick Play",
                badgeColor = Color(0xFF00897B),
                onClick = { onNavigateToGame("game_need_want") }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Achievements Bottom Sheet
    if (showAchievementsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAchievementsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 Game Achievements",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${gameState.unlockedAchievements.size} / ${GameConstants.ACHIEVEMENTS.size} Unlocked",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(GameConstants.ACHIEVEMENTS) { ach ->
                        val isUnlocked = gameState.unlockedAchievements.contains(ach.id)

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isUnlocked) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) ach.icon else "🔒",
                                    fontSize = 28.sp
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ach.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = ach.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("⭐ +${ach.xpReward} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                                    Text("🪙 +${ach.coinReward} Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB78103))
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { showAchievementsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Reward Celebration Modal
    rewardPopup?.let { reward ->
        RewardCelebrationDialog(
            reward = reward,
            onDismiss = { viewModel.dismissRewardPopup() }
        )
    }
}

@Composable
fun DailyChallengeSection(
    viewModel: MoneyGamesViewModel,
    completedChallenges: Set<String>
) {
    val dailyList = viewModel.dailyChallenges

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎯", fontSize = 18.sp)
                    Text("Today's Financial Quests", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Daily Reset",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            dailyList.take(2).forEach { challenge ->
                val isCompleted = completedChallenges.contains(challenge.id)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(challenge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (isCompleted) {
                            Text("✅ Done", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                        } else {
                            Button(
                                onClick = { viewModel.claimDailyChallenge(challenge) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("+${challenge.coinReward}🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
