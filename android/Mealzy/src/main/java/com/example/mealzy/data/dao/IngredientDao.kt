package com.example.mealzy.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mealzy.data.model.Ingredient

@Dao
interface IngredientDao {
    
    @Query("SELECT * FROM ingredients ORDER BY category, name")
    fun getAllIngredients(): LiveData<List<Ingredient>>
    
    @Query("SELECT * FROM ingredients WHERE category = :category ORDER BY name")
    fun getIngredientsByCategory(category: String): LiveData<List<Ingredient>>
    
    @Query("SELECT * FROM ingredients WHERE isAvailable = 1 ORDER BY name")
    fun getAvailableIngredients(): LiveData<List<Ingredient>>
    
    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getIngredientById(id: Long): Ingredient?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<Ingredient>)
    
    @Update
    suspend fun updateIngredient(ingredient: Ingredient)
    
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)
    
    @Query("DELETE FROM ingredients WHERE id = :id")
    suspend fun deleteIngredientById(id: Long)
    
    @Query("SELECT DISTINCT category FROM ingredients ORDER BY category")
    fun getCategories(): LiveData<List<String>>

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun getIngredientCount(): Int
}