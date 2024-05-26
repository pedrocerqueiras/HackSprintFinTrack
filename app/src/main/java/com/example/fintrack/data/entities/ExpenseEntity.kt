package com.example.fintrack.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity // Anotação para definir que esta classe é uma entidade de banco de dados
    (
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["key"],
            childColumns = ["category"]
        )
    ]
)

@Parcelize // Anotação para permitir a serialização
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Double,
) : Parcelable // Implementação da interface Parcelable para permitir que objetos dessa classe sejam passados através de intents