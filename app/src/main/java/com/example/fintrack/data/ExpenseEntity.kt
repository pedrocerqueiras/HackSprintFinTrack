package com.example.fintrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Float,
)
