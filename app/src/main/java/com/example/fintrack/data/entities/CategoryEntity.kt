package com.example.fintrack.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo("key")
    val name: String,
    @ColumnInfo ("is_selected")
    val isSelected: Boolean,
    @ColumnInfo (name = "icon")
    val iconResId: Int,
    @ColumnInfo (name = "color")
    val color: Int
): Parcelable


