package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CategoryEntity::class, TransactionEntity::class, BudgetEntity::class, GoalEntity::class, HabitLogEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.budgetDao())
                }
            }
        }

        suspend fun populateDatabase(dao: BudgetDao) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Food", color = 0xFFE57373, isDefault = true),
                CategoryEntity(name = "Transport", color = 0xFF81C784, isDefault = true),
                CategoryEntity(name = "Shopping", color = 0xFF64B5F6, isDefault = true),
                CategoryEntity(name = "Bills", color = 0xFFFFD54F, isDefault = true),
                CategoryEntity(name = "Entertainment", color = 0xFFBA68C8, isDefault = true),
                CategoryEntity(name = "Education", color = 0xFF4DB6AC, isDefault = true),
                CategoryEntity(name = "Healthcare", color = 0xFFFF8A65, isDefault = true),
                CategoryEntity(name = "Salary", isIncome = true, color = 0xFFAED581, isDefault = true),
                CategoryEntity(name = "Investments", isIncome = true, color = 0xFF4DD0E1, isDefault = true),
                CategoryEntity(name = "Others", color = 0xFF90A4AE, isDefault = true)
            )
            defaultCategories.forEach { dao.insertCategory(it) }
        }
    }
}
