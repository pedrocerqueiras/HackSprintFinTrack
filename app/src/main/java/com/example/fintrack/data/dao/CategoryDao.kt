package com.example.fintrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categoryentity")
    fun getAllCategories(): List<CategoryEntity>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAll (categoryEntity: List<CategoryEntity>)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert (categoryEntity: CategoryEntity)

}