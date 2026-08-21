package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.BudgetRepository
import com.example.ui.games.data.GameRepository

class MainApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BudgetRepository(database.budgetDao()) }
    val gameRepository by lazy { GameRepository(this) }
}
