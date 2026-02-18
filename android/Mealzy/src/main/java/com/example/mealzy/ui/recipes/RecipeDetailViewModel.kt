package com.example.mealzy.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.database.MealzyDatabase
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.data.model.RecipeIngredientDetail
import com.example.mealzy.data.repository.MealzyRepository
import kotlinx.coroutines.launch

class RecipeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository = MealzyDatabase.getDatabase(application).let { db ->
        MealzyRepository(db.ingredientDao(), db.recipeDao(), db.recipeIngredientDao(), db.mealPlanDao())
    }

    private val _recipeId = MutableLiveData<Long>()
    val ingredients: LiveData<List<RecipeIngredientDetail>> = _recipeId.switchMap { id ->
        repository.getIngredientDetailsForRecipe(id)
    }

    fun loadIngredients(recipeId: Long) {
        _recipeId.value = recipeId
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }
}
