package com.example.mealzy.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.mealzy.data.database.MealzyDatabase
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.data.repository.MealzyRepository

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository
    private val allRecipesSource: LiveData<List<Recipe>>

    val recipes = MediatorLiveData<List<Recipe>>()

    private val _searchQuery = MutableLiveData<String?>()
    private val _mealTypeFilter = MutableLiveData<MealType?>()

    init {
        val database = MealzyDatabase.getDatabase(application)
        repository = MealzyRepository(
            database.ingredientDao(),
            database.recipeDao(),
            database.recipeIngredientDao(),
            database.mealPlanDao()
        )
        allRecipesSource = repository.getAllRecipes()
        recipes.addSource(allRecipesSource) { applyFilters() }
        recipes.addSource(_searchQuery) { applyFilters() }
        recipes.addSource(_mealTypeFilter) { applyFilters() }
    }

    fun searchRecipes(query: String) {
        _searchQuery.value = query.trim().takeIf { it.isNotEmpty() }
    }

    fun clearSearch() {
        _searchQuery.value = null
    }

    fun filterByMealType(mealType: MealType?) {
        _mealTypeFilter.value = mealType
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }

    private fun applyFilters() {
        var filtered = allRecipesSource.value ?: emptyList()

        _mealTypeFilter.value?.let { mealType ->
            filtered = filtered.filter { it.mealType == mealType }
        }

        _searchQuery.value?.let { query ->
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        recipes.value = filtered
    }
}
