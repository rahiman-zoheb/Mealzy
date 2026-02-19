package com.example.mealzy.ui.ingredients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.database.MealzyDatabase
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.data.model.RecipeIngredient
import com.example.mealzy.data.repository.MealzyRepository
import kotlinx.coroutines.launch

enum class FilterMode {
    ALL,
    AVAILABLE_ONLY,
    OUT_OF_STOCK
}

class IngredientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository
    private val allIngredientsSource: LiveData<List<Ingredient>>

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filterMode = MutableLiveData<FilterMode>(FilterMode.ALL)
    val filterMode: LiveData<FilterMode> = _filterMode

    val ingredients = MediatorLiveData<List<IngredientListItem>>()

    init {
        val database = MealzyDatabase.getDatabase(application)
        repository = MealzyRepository(
            database.ingredientDao(),
            database.recipeDao(),
            database.recipeIngredientDao(),
            database.mealPlanDao()
        )
        allIngredientsSource = repository.getAllIngredients()
        setupFiltering()
    }

    private fun setupFiltering() {
        ingredients.addSource(allIngredientsSource) { updateFilteredIngredients() }
        ingredients.addSource(_searchQuery) { updateFilteredIngredients() }
        ingredients.addSource(_filterMode) { updateFilteredIngredients() }
    }

    private fun updateFilteredIngredients() {
        val allItems = allIngredientsSource.value ?: emptyList()
        val query = _searchQuery.value ?: ""
        val mode = _filterMode.value ?: FilterMode.ALL

        var filtered = allItems

        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        filtered = when (mode) {
            FilterMode.AVAILABLE_ONLY -> filtered.filter { it.isAvailable }
            FilterMode.OUT_OF_STOCK -> filtered.filter { !it.isAvailable }
            FilterMode.ALL -> filtered
        }

        filtered = filtered.sortedWith(compareBy<Ingredient> { it.category }.thenBy { it.name })

        val items = mutableListOf<IngredientListItem>()
        var currentCategory: String? = null
        for (ingredient in filtered) {
            if (ingredient.category != currentCategory) {
                currentCategory = ingredient.category
                items.add(IngredientListItem.Header(currentCategory))
            }
            items.add(IngredientListItem.Item(ingredient))
        }

        ingredients.value = items
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterMode(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.insertIngredient(ingredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.updateIngredient(ingredient)
        }
    }

    fun toggleIngredientAvailability(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.updateIngredient(ingredient.copy(isAvailable = !ingredient.isAvailable))
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }

    fun deleteIngredientWithLinks(ingredient: Ingredient, onDeleted: (List<RecipeIngredient>) -> Unit) {
        viewModelScope.launch {
            val links = repository.getRecipeIngredientsForIngredient(ingredient.id)
            repository.deleteIngredient(ingredient)
            onDeleted(links)
        }
    }

    fun restoreIngredientWithLinks(ingredient: Ingredient, links: List<RecipeIngredient>) {
        viewModelScope.launch {
            repository.insertIngredient(ingredient)
            if (links.isNotEmpty()) {
                repository.insertRecipeIngredients(links)
            }
        }
    }
}
