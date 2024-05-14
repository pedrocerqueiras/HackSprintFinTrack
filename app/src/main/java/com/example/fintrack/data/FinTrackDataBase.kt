package com.example.fintrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao

@Database(entities = [CategoryEntity::class, ExpenseEntity::class], version = 1, exportSchema = false)
abstract class FinTrackDataBase: RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun expenseDao(): ExpenseDao

}