package com.example.fintrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao
import com.example.fintrack.data.entities.CategoryEntity
import com.example.fintrack.data.entities.ExpenseEntity
import com.example.fintrack.util.Converters

@Database([CategoryEntity::class, ExpenseEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class FinTrackDataBase: RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao

    abstract fun getExpenseDao(): ExpenseDao

    companion object {
        // Anotação Volatile para garantir que a variável seja visível para todos os threads.
        @Volatile
        private var INSTANCE: FinTrackDataBase? = null

        // Função para obter a instância do banco de dados, criando-a se necessário.
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