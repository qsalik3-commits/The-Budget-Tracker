package com.example.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.add.AddTransactionScreen
import com.example.ui.chat.ChatScreen
import com.example.ui.home.DashboardScreen
import com.example.ui.reports.ReportsScreen
import com.example.ui.goals.GoalsScreen
import com.example.ui.calculators.CalculatorsScreen

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Dashboard : Screen("dashboard", "Dashboard", { Icon(Icons.Filled.Home, contentDescription = "Dashboard") })
    object Reports : Screen("reports", "Reports", { Icon(Icons.Filled.Analytics, contentDescription = "Reports") })
    object Goals : Screen("goals", "Goals", { Icon(Icons.Filled.Flag, contentDescription = "Goals") })
    object Calculators : Screen("calculators", "Tools", { Icon(Icons.Filled.Calculate, contentDescription = "Tools") })
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    budgetViewModel: BudgetViewModel,
    chatViewModel: ChatViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(Screen.Dashboard, Screen.Reports, Screen.Goals, Screen.Calculators)
    val bottomBarDestination = items.any { it.route == currentDestination?.route }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (bottomBarDestination) {
                NavigationBar(
                    modifier = Modifier.height(72.dp),
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF3EDF7),
                    contentColor = androidx.compose.ui.graphics.Color(0xFF1D192B),
                    windowInsets = WindowInsets(0.dp)
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            modifier = Modifier.padding(top = 4.dp),
                            icon = screen.icon,
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.ui.graphics.Color(0xFF1D192B),
                                selectedTextColor = androidx.compose.ui.graphics.Color(0xFF1D192B),
                                indicatorColor = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
                                unselectedIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                                unselectedTextColor = androidx.compose.ui.graphics.Color(0xFF49454F)
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = budgetViewModel,
                    onNavigateToAdd = { navController.navigate("add_transaction") },
                    onNavigateToChat = { navController.navigate("chat") }
                )
            }
            composable(Screen.Reports.route) {
                ReportsScreen(viewModel = budgetViewModel)
            }
            composable(Screen.Goals.route) {
                GoalsScreen(viewModel = budgetViewModel)
            }
            composable(Screen.Calculators.route) {
                CalculatorsScreen(viewModel = budgetViewModel)
            }
            composable("add_transaction") {
                AddTransactionScreen(
                    viewModel = budgetViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("chat") {
                ChatScreen(
                    viewModel = chatViewModel,
                    budgetViewModel = budgetViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
