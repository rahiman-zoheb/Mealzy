package com.example.mealzy.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.data.model.MealType

@Dao
interface RecipeDao {
    
    @Query("SELECT * FROM recipes ORDER BY name")
    fun getAllRecipes(): LiveData<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE mealType = :mealType ORDER BY name")
    fun getRecipesByMealType(mealType: MealType): LiveData<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?
    
    @Query("SELECT * FROM recipes WHERE name LIKE :searchQuery ORDER BY name")
    fun searchRecipes(searchQuery: String): LiveData<List<Recipe>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
    
    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Long)
}