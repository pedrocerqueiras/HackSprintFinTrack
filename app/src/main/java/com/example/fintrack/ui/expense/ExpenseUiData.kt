package com.example.fintrack.ui.expense

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class ExpenseUiData(
    val id: Long,
    val category: String,
    val name: String,
    val amount: Double,
    val date: Date,
    val iconResId: Int,
    var color: Int
):Parcelable
