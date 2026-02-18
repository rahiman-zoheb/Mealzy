package com.example.mealzy.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mealzy.data.model.RecipeIngredient
import com.example.mealzy.data.model.RecipeIngredientDetail

@Dao
interface RecipeIngredientDao {

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): LiveData<List<RecipeIngredient>>

    @Query("""
        SELECT ri.ingredientId, i.name AS ingredientName, ri.quantity, ri.unit,
               i.isAvailable, i.category
        FROM recipe_ingredients ri
        INNER JOIN ingredients i ON ri.ingredientId = i.id
        WHERE ri.recipeId = :recipeId
        ORDER BY i.category, i.name
    """)
    fun getIngredientDetailsForRecipe(recipeId: Long): LiveData<List<RecipeIngredientDetail>>

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
