package com.example.ui.games.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.games.MoneyGamesViewModel
import com.example.ui.games.data.BuildingDefinition
import com.example.ui.games.data.CityBuildingState
import com.example.ui.games.data.CityEvent
import com.example.ui.games.data.GameConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyCityGameScreen(
    viewModel: MoneyGamesViewModel,
    onBack: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val activeEvent by viewModel.activeCityEvent.collectAsStateWithLifecycle()

    var showBuildCatalog by remember { mutableStateOf(false) }
    var selectedBuildingToUpgrade by remember { mutableStateOf<Pair<CityBuildingState, BuildingDefinition>?>(null) }
    var revenueCollectedFeedback by remember { mutableStateOf<Int?>(null) }

    val totalBuildings = gameState.cityBuildings.size
    val totalIncomePerMin = gameState.cityBuildings.sumOf { b ->
        val def = GameConstants.ALL_BUILDINGS.find { it.typeId == b.typeId }
        (def?.baseIncomePerMin ?: 0) * b.level
    }
    val totalPopulation = 25 + gameState.cityBuildings.sumOf { b ->
        val def = GameConstants.ALL_BUILDINGS.find { it.typeId == b.typeId }
        (def?.populationBonus ?: 0) * b.level
    }

    val cityTier = when {
        totalBuildings >= 15 -> "👑 Financial Empire"
        totalBuildings >= 10 -> "🌆 Mega City"
        totalBuildings >= 6 -> "🏙️ City"
        totalBuildings >= 3 -> "🏡 Town"
        else -> "🌱 Village"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏙️ Build Your Money City", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Random Virtual Event Trigger
                    IconButton(onClick = { viewModel.triggerRandomCityEvent() }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "City Event", tint = Color(0xFFFFD54F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B182B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showBuildCatalog = true },
                containerColor = Color(0xFFFFD54F),
                contentColor = Color(0xFF261800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.AddBusiness, contentDescription = "Construct")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Construct Building", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF12101F))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // City Header Billboard
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242038)),
                border = BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Metropolis: $cityTier",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Player Level ${gameState.level} • ${gameState.levelTitle}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBC4CF)
                            )
                        }

                        Surface(
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🪙", fontSize = 14.sp)
                                Text(
                                    "${gameState.virtualCoins}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color(0xFF261800)
                                )
                            }
                        }
                    }

                    // Key Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CityStatChip(
                            label = "Income",
                            value = "+$totalIncomePerMin/min",
                            icon = "📈",
                            modifier = Modifier.weight(1f)
                        )
                        CityStatChip(
                            label = "Citizens",
                            value = "$totalPopulation",
                            icon = "👥",
                            modifier = Modifier.weight(1f)
                        )
                        CityStatChip(
                            label = "Buildings",
                            value = "$totalBuildings",
                            icon = "🏛️",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Collect Revenue Button
                    Button(
                        onClick = {
                            val revenue = viewModel.collectCityRevenue()
                            revenueCollectedFeedback = revenue
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Collect")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Collect City Taxes & Revenue", fontWeight = FontWeight.Bold)
                    }

                    // Revenue feedback toast
                    revenueCollectedFeedback?.let { amt ->
                        Surface(
                            color = if (amt > 0) Color(0xFF2E7D32) else Color(0xFF455A64),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (amt > 0) "💰 Collected +$amt Virtual Coins!" else "✨ City treasury is already up to date!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // City Interactive Grid / Building Lots
            Text(
                text = "City Skyline & District (${gameState.cityBuildings.size} Built)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            if (gameState.cityBuildings.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🏗️", fontSize = 40.sp)
                        Text("No buildings constructed yet!", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "Tap 'Construct Building' below to start building your town with your 500 starting coins!",
                            color = Color(0xFFCBC4CF),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Display 2-column grid of built structures
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    gameState.cityBuildings.chunked(2).forEach { rowBuildings ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowBuildings.forEach { bState ->
                                val def = GameConstants.ALL_BUILDINGS.find { it.typeId == bState.typeId }
                                    ?: GameConstants.ALL_BUILDINGS[0]

                                BuildingGridCard(
                                    state = bState,
                                    definition = def,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedBuildingToUpgrade = Pair(bState, def)
                                    }
                                )
                            }
                            if (rowBuildings.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp)) // Padding for FAB
        }
    }

    // Build Catalog Modal / Dialog
    if (showBuildCatalog) {
        AlertDialog(
            onDismissRequest = { showBuildCatalog = false },
            confirmButton = {
                TextButton(onClick = { showBuildCatalog = false }) {
                    Text("Close")
                }
            },
            title = {
                Text("🏗️ Construct New Building", fontWeight = FontWeight.Bold)
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(GameConstants.ALL_BUILDINGS) { def ->
                        val canAfford = gameState.virtualCoins >= def.baseCost
                        val isLevelLocked = gameState.level < def.unlockLevel

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLevelLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(def.icon, fontSize = 32.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(def.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "Income: +${def.baseIncomePerMin}/min • ${def.category}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (isLevelLocked) {
                                        Text("🔒 Unlocks at Player Lvl ${def.unlockLevel}", fontSize = 11.sp, color = Color(0xFFC62828))
                                    }
                                }

                                Button(
                                    onClick = {
                                        val success = viewModel.buildCityStructure(def.typeId)
                                        if (success) {
                                            showBuildCatalog = false
                                        }
                                    },
                                    enabled = canAfford && !isLevelLocked,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("🪙 ${def.baseCost}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // Upgrade Building Modal
    selectedBuildingToUpgrade?.let { (bState, def) ->
        val upgradeCost = def.baseCost * (bState.level + 1) / 2
        val canAffordUpgrade = gameState.virtualCoins >= upgradeCost
        val isMaxLevel = bState.level >= 5

        AlertDialog(
            onDismissRequest = { selectedBuildingToUpgrade = null },
            confirmButton = {
                if (!isMaxLevel) {
                    Button(
                        onClick = {
                            viewModel.upgradeCityStructure(bState.id)
                            selectedBuildingToUpgrade = null
                        },
                        enabled = canAffordUpgrade,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Upgrade (🪙 $upgradeCost)", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBuildingToUpgrade = null }) {
                    Text("Close")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(def.icon, fontSize = 24.sp)
                    Text("${def.name} (Lvl ${bState.level})", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(def.description, style = MaterialTheme.typography.bodyMedium)
                    Text("Current Revenue: +${def.baseIncomePerMin * bState.level} 🪙/min", fontWeight = FontWeight.Bold)
                    if (!isMaxLevel) {
                        Text("Next Level (Lvl ${bState.level + 1}): +${def.baseIncomePerMin * (bState.level + 1)} 🪙/min", color = Color(0xFF2E7D32))
                        Text("Upgrade Cost: $upgradeCost virtual coins", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text("⭐ MAX LEVEL REACHED!", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                    }
                }
            }
        )
    }

    // Random Virtual Event Popup
    activeEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCityEvent() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissCityEvent() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F), contentColor = Color(0xFF261800))
                ) {
                    Text("Claim Bonus!", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(event.icon, fontSize = 24.sp)
                    Text(event.title, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(event.description, style = MaterialTheme.typography.bodyMedium)
                    Surface(
                        color = Color(0xFFEDE7F6),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🎁 City Bonus: +${event.coinBonus} Coins | +${event.xpBonus} XP",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun CityStatChip(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF312C4A)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 14.sp)
            Text(value, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            Text(label, fontSize = 10.sp, color = Color(0xFFCBC4CF))
        }
    }
}

@Composable
fun BuildingGridCard(
    state: CityBuildingState,
    definition: BuildingDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E)),
        border = BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF7C4DFF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Lvl ${state.level}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text("🪙 +${definition.baseIncomePerMin * state.level}/m", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = definition.icon,
                fontSize = 38.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = definition.name,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = "Tap to Upgrade ➔",
                fontSize = 10.sp,
                color = Color(0xFFB388FF),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
