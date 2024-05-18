package com.example.fintrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.ExpenseEntity

@Dao
interface ExpenseDao {

    @Query ("SELECT * FROM expenseentity")
    fun getAllExpenses(): List<ExpenseEntity>
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAll (expenseEntities: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expenseEntity: ExpenseEntity)

    @Update
    fun update (expenseEntity: ExpenseEntity)

    @Delete
    fun delete (expenseEntity: ExpenseEntity)

}