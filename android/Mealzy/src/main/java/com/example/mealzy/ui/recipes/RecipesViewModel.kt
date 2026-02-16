package com.example.mealzy.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import kotlinx.coroutines.launch

class RecipesViewModel : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var allRecipes: List<Recipe> = emptyList()
    private var currentSearchQuery: String? = null
    private var currentMealTypeFilter: MealType? = null

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            // TODO: Load from repository
            // For now, create sample data
            allRecipes = createSampleRecipes()
            applyFilters()
            _isLoading.value = false
        }
    }

    fun searchRecipes(query: String) {
        currentSearchQuery = query.trim().takeIf { it.isNotEmpty() }
        applyFilters()
    }

    fun clearSearch() {
        currentSearchQuery = null
        applyFilters()
    }

    fun filterByMealType(mealType: MealType?) {
        currentMealTypeFilter = mealType
        applyFilters()
    }

    private fun applyFilters() {
        var filteredRecipes = allRecipes

        // Apply meal type filter
        currentMealTypeFilter?.let { mealType ->
            filteredRecipes = filteredRecipes.filter { it.mealType == mealType }
        }

        // Apply search filter
        currentSearchQuery?.let { query ->
            filteredRecipes = filteredRecipes.filter { 
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        _recipes.value = filteredRecipes
    }

    private fun createSampleRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                name = "Scrambled Eggs",
                description = "Classic breakfast scrambled eggs",
                instructions = "1. Crack eggs into bowl\n2. Whisk with milk\n3. Cook in pan",
                prepTimeMinutes = 5,
                cookTimeMinutes = 10,
                servings = 2,
                difficulty = "Easy",
                mealType = MealType.BREAKFAST
            ),
            Recipe(
                id = 2,
                name = "Chicken Stir Fry",
                description = "Quick and healthy chicken stir fry",
                instructions = "1. Cut chicken\n2. Heat oil in wok\n3. Stir fry with vegetables",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                servings = 4,
                difficulty = "Medium",
                mealType = MealType.LUNCH
            ),
            Recipe(
                id = 3,
                name = "Spaghetti Bolognese",
                description = "Traditional Italian pasta dish",
                instructions = "1. Cook pasta\n2. Prepare meat sauce\n3. Combine and serve",
                prepTimeMinutes = 20,
                cookTimeMinutes = 45,
                servings = 4,
                difficulty = "Medium",
                mealType = MealType.DINNER
            ),
            Recipe(
                id = 4,
                name = "Greek Yogurt Parfait",
                description = "Healthy yogurt with berries and granola",
                instructions = "1. Layer yogurt\n2. Add berries\n3. Top with granola",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                servings = 1,
                difficulty = "Easy",
                mealType = MealType.SNACK
            )
        )
    }
}