package com.example.fintrack.ui.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryUiData(
    val name: String,
    val isSelected: Boolean,
    val icon: Int,
    val color: Int
):Parcelable