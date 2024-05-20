package com.example.fintrack.ui.expense

data class ExpenseUiData(
    val id: Long,
    val category: String,
    val name: String,
    val amount: Float,
    val iconResId: Int,
    var color: Int
)
