package com.example.fintrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao
import com.example.fintrack.data.entities.CategoryEntity
import com.example.fintrack.data.entities.ExpenseEntity

@Database([CategoryEntity::class, ExpenseEntity::class], version = 3)
abstract class FinTrackDataBase: RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao

    abstract fun getExpenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: FinTrackDataBase? = null

        fun getInstance(context: Context): FinTrackDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinTrackDataBase::class.java,
                    "fintrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}