package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.BudgetRepository

class MainApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BudgetRepository(database.budgetDao()) }
}
