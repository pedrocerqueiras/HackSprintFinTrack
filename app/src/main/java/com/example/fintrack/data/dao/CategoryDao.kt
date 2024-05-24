package com.example.fintrack.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fintrack.data.entities.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categoryentity")
    fun getAllCategories(): List<CategoryEntity>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAll (categoryEntity: List<CategoryEntity>)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert (categoryEntity: CategoryEntity)

    @Delete
    fun delete (categoryEntity: CategoryEntity)

}