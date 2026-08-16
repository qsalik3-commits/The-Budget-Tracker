package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavigation
import com.example.ui.BudgetViewModel
import com.example.ui.BudgetViewModelFactory
import com.example.ui.ChatViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val app = application as MainApplication
    val factory = BudgetViewModelFactory(app.repository)
    
    setContent {
      MyApplicationTheme {
        val budgetViewModel: BudgetViewModel = viewModel(factory = factory)
        val chatViewModel: ChatViewModel = viewModel(factory = factory)
        
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          AppNavigation(
            modifier = Modifier.padding(innerPadding),
            budgetViewModel = budgetViewModel,
            chatViewModel = chatViewModel
          )
        }
      }
    }
  }
}
