package com.example.fintrack.ui.expense

import java.util.Date

data class ExpenseUiData(
    val id: Long,
    val category: String,
    val name: String,
    val amount: Double,
    val date: Date,
    val iconResId: Int,
    var color: Int
)
