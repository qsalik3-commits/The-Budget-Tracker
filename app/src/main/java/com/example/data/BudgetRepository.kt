package com.example.data

import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val allTransactions: Flow<List<TransactionWithCategory>> = dao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val allHabitLogs: Flow<List<HabitLogEntity>> = dao.getAllHabitLogs()

    suspend fun insertCategory(category: CategoryEntity) = dao.insertCategory(category)
    suspend fun insertTransaction(transaction: TransactionEntity) = dao.insertTransaction(transaction)
    suspend fun insertBudget(budget: BudgetEntity) = dao.insertBudget(budget)
    suspend fun insertGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun addGoalProgress(id: Int, amount: Double) = dao.addGoalProgress(id, amount)
    suspend fun insertHabitLog(log: HabitLogEntity) = dao.insertHabitLog(log)
    suspend fun deleteHabitLog(habitName: String, date: String) = dao.deleteHabitLog(habitName, date)
}
