package com.example.fintrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao

@Database([CategoryEntity::class, ExpenseEntity::class], version = 3)
abstract class FinTrackDataBase: RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao

    abstract fun getExpenseDao(): ExpenseDao

}