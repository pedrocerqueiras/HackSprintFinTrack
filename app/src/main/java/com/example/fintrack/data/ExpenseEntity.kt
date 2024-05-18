package com.example.fintrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["key"],
            childColumns = ["category"]
        )
    ]
)

data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Float,
)
