package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class TransactionWithCategory(
    val id: Int,
    val amount: Double,
    val note: String,
    val timestamp: Long,
    val isIncome: Boolean,
    val categoryId: Int,
    val categoryName: String,
    val categoryColor: Long
)

@Dao
interface BudgetDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("""
        SELECT t.id, t.amount, t.note, t.timestamp, t.isIncome, c.id as categoryId, c.name as categoryName, c.color as categoryColor 
        FROM transactions t INNER JOIN categories c ON t.categoryId = c.id
        ORDER BY t.timestamp DESC
    """)
    fun getAllTransactions(): Flow<List<TransactionWithCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)
    
    @Query("UPDATE goals SET currentAmount = currentAmount + :amount WHERE id = :id")
    suspend fun addGoalProgress(id: Int, amount: Double)

    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitName = :habitName AND date = :date")
    suspend fun deleteHabitLog(habitName: String, date: String)
}
