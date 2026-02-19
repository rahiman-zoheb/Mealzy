package com.example.mealzy.data.repository

import androidx.lifecycle.LiveData
import com.example.mealzy.data.dao.IngredientDao
import com.example.mealzy.data.dao.RecipeDao
import com.example.mealzy.data.dao.RecipeIngredientDao
import com.example.mealzy.data.dao.MealPlanDao
import com.example.mealzy.data.model.*
import com.example.mealzy.data.model.RecipeIngredientDetail
import java.util.Date

class MealzyRepository(
    private val ingredientDao: IngredientDao,
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val mealPlanDao: MealPlanDao
) {
    
    // Ingredient operations
    fun getAllIngredients(): LiveData<List<Ingredient>> = ingredientDao.getAllIngredients()
    
    fun getIngredientsByCategory(category: String): LiveData<List<Ingredient>> = 
        ingredientDao.getIngredientsByCategory(category)
    
    fun getAvailableIngredients(): LiveData<List<Ingredient>> = 
        ingredientDao.getAvailableIngredients()
    
    suspend fun insertIngredient(ingredient: Ingredient): Long = 
        ingredientDao.insertIngredient(ingredient)
    
    suspend fun updateIngredient(ingredient: Ingredient) = 
        ingredientDao.updateIngredient(ingredient)
    
    suspend fun deleteIngredient(ingredient: Ingredient) = 
        ingredientDao.deleteIngredient(ingredient)
    
    fun getCategories(): LiveData<List<String>> = ingredientDao.getCategories()
    
    // Recipe operations
    fun getAllRecipes(): LiveData<List<Recipe>> = recipeDao.getAllRecipes()
    
    fun getRecipesByMealType(mealType: MealType): LiveData<List<Recipe>> = 
        recipeDao.getRecipesByMealType(mealType)
    
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes()
    
    fun searchRecipes(query: String): LiveData<List<Recipe>> = 
        recipeDao.searchRecipes("%$query%")
    
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    
    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)

    // Recipe ingredient operations
    fun getIngredientsForRecipe(recipeId: Long): LiveData<List<RecipeIngredient>> =
        recipeIngredientDao.getIngredientsForRecipe(recipeId)

    fun getIngredientDetailsForRecipe(recipeId: Long): LiveData<List<RecipeIngredientDetail>> =
        recipeIngredientDao.getIngredientDetailsForRecipe(recipeId)

    suspend fun toggleFavorite(recipe: Recipe) =
        recipeDao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))

    suspend fun getIngredientsForRecipeSync(recipeId: Long): List<RecipeIngredient> =
        recipeIngredientDao.getIngredientsForRecipeSync(recipeId)

    suspend fun getAvailableIngredientsForRecipe(recipeId: Long): List<RecipeIngredient> =
        recipeIngredientDao.getAvailableIngredientsForRecipe(recipeId)

    suspend fun getRecipeIngredientsForIngredient(ingredientId: Long): List<RecipeIngredient> =
        recipeIngredientDao.getRecipeIngredientsForIngredient(ingredientId)

    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient) =
        recipeIngredientDao.insertRecipeIngredient(recipeIngredient)

    suspend fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>) =
        recipeIngredientDao.insertRecipeIngredients(recipeIngredients)

    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient) =
        recipeIngredientDao.deleteRecipeIngredient(recipeIngredient)

    suspend fun deleteAllIngredientsForRecipe(recipeId: Long) =
        recipeIngredientDao.deleteAllIngredientsForRecipe(recipeId)

    // Meal plan operations
    fun getAllMealPlans(): LiveData<List<MealPlan>> = mealPlanDao.getAllMealPlans()
    
    fun getMealPlansInRange(startDate: Date, endDate: Date): LiveData<List<MealPlan>> = 
        mealPlanDao.getMealPlansInRange(startDate, endDate)
    
    suspend fun getMealPlanByDateAndType(dayStart: Date, dayEnd: Date, mealType: MealType): MealPlan? =
        mealPlanDao.getMealPlanByDateAndType(dayStart, dayEnd, mealType)

    suspend fun insertMealPlan(mealPlan: MealPlan): Long = mealPlanDao.insertMealPlan(mealPlan)
    
    suspend fun updateMealPlan(mealPlan: MealPlan) = mealPlanDao.updateMealPlan(mealPlan)
    
    suspend fun deleteMealPlan(mealPlan: MealPlan) = mealPlanDao.deleteMealPlan(mealPlan)
}