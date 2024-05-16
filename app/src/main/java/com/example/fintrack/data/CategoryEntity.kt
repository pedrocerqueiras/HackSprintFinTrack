package com.example.fintrack.data

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo("categorykey")
    val name: String,
    @ColumnInfo ("is_selected")
    val isSelected: Boolean
)

