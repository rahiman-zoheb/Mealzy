package com.example.mealzy.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mealzy.data.model.RecipeIngredient

@Dao
interface RecipeIngredientDao {

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): LiveData<List<RecipeIngredient>>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getIngredientsForRecipeSync(recipeId: Long): List<RecipeIngredient>

    @Query("""
        SELECT DISTINCT ri.* FROM recipe_ingredients ri
        INNER JOIN ingredients i ON ri.ingredientId = i.id
        WHERE i.isAvailable = 1 AND ri.recipeId = :recipeId
    """)
    suspend fun getAvailableIngredientsForRecipe(recipeId: Long): List<RecipeIngredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>)

    @Delete
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteAllIngredientsForRecipe(recipeId: Long)
}
