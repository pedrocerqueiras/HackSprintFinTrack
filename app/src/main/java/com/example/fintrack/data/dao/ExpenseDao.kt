package com.example.fintrack.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.entities.ExpenseEntity
import java.util.Calendar

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

    @Query("SELECT * FROM expenseentity WHERE date = :selectedDate")
    fun getExpensesByDate(selectedDate: Long): List<ExpenseEntity>

    @Query("SELECT * FROM expenseentity WHERE date >= :startDate AND date <= :endDate")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): List<ExpenseEntity>

    @Query ("SELECT * FROM expenseentity where category is :categoryName")
    fun getAllByCategoryName(categoryName: String): List<ExpenseEntity>

    @Query("SELECT * FROM expenseentity WHERE category = :categoryName AND date BETWEEN :startDate AND :endDate")
    fun getExpensesByCategoryAndDateRange(categoryName: String, startDate: Long, endDate: Long): List<ExpenseEntity>

    @Delete
    fun deleteAll (expenseEntity: List<ExpenseEntity>)

}