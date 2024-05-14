package com.example.fintrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.ExpenseEntity

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: ExpenseEntity)

    @Update
    suspend fun update (expense: ExpenseEntity)

    @Query ("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<ExpenseEntity>

}