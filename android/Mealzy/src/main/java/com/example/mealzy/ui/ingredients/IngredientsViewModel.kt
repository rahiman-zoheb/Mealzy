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
    OUT_OF_STOCK,
    LOW_STOCK
}

data class IngredientCounts(val total: Int, val available: Int, val outOfStock: Int, val lowStock: Int)

sealed class EmptyStateVariant {
    object NoIngredients : EmptyStateVariant()
    data class NoSearchResults(val query: String) : EmptyStateVariant()
    data class NoFilterResults(val filter: FilterMode) : EmptyStateVariant()
    data class NoCategoryResults(val category: String) : EmptyStateVariant()
}

class IngredientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository
    private val allIngredientsSource: LiveData<List<Ingredient>>

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filterMode = MutableLiveData<FilterMode>(FilterMode.ALL)
    val filterMode: LiveData<FilterMode> = _filterMode

    // null = show all categories
    private val _categoryFilter = MutableLiveData<String?>(null)
    val categoryFilter: LiveData<String?> = _categoryFilter

    val ingredients = MediatorLiveData<List<IngredientListItem>>()

    // Status chip counts — reflect the active category filter so counts are contextual
    private val _ingredientCounts = MutableLiveData<IngredientCounts>(IngredientCounts(0, 0, 0, 0))
    val ingredientCounts: LiveData<IngredientCounts> = _ingredientCounts

    // Category chip counts — always from all ingredients, stable reference
    private val _categoryCounts = MutableLiveData<Map<String, Int>>(emptyMap())
    val categoryCounts: LiveData<Map<String, Int>> = _categoryCounts

    private val _emptyStateVariant = MutableLiveData<EmptyStateVariant?>()
    val emptyStateVariant: LiveData<EmptyStateVariant?> = _emptyStateVariant

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
        ingredients.addSource(allIngredientsSource) { list ->
            updateCategoryCounts(list ?: emptyList())
            updateFilteredIngredients()
        }
        ingredients.addSource(_searchQuery) { updateFilteredIngredients() }
        ingredients.addSource(_filterMode) { updateFilteredIngredients() }
        ingredients.addSource(_categoryFilter) { updateFilteredIngredients() }
    }

    // Category counts use ALL ingredients — stable, not affected by status or search filter
    private fun updateCategoryCounts(allItems: List<Ingredient>) {
        _categoryCounts.value = allItems.groupBy { it.category }.mapValues { it.value.size }
    }

    // Status counts are scoped to the active category so they stay meaningful
    private fun updateIngredientCounts(scopedItems: List<Ingredient>) {
        val total = scopedItems.size
        val available = scopedItems.count { it.isAvailable }
        val outOfStock = scopedItems.count { !it.isAvailable }
        val lowStock = scopedItems.count { isLowStock(it) && it.isAvailable }
        _ingredientCounts.value = IngredientCounts(total, available, outOfStock, lowStock)
    }

    fun isLowStock(ingredient: Ingredient): Boolean {
        val quantity = ingredient.quantity.toDoubleOrNull() ?: return false
        return when (ingredient.unit.lowercase()) {
            "pieces", "pcs"   -> quantity <= 1.0
            "cups"            -> quantity <= 0.25
            "lbs", "kg"       -> quantity <= 0.25
            "oz", "grams"     -> quantity <= 50.0
            "liters"          -> quantity <= 0.25
            "ml"              -> quantity <= 100.0
            "tbsp", "tsp"     -> quantity <= 1.0
            "bottles", "cans" -> quantity <= 1.0
            else              -> quantity <= 1.0
        }
    }

    private fun updateFilteredIngredients() {
        val allItems = allIngredientsSource.value ?: emptyList()
        val query = _searchQuery.value ?: ""
        val mode = _filterMode.value ?: FilterMode.ALL
        val cat = _categoryFilter.value

        // Apply category filter first; status counts are scoped to this subset
        val categoryScoped = if (cat != null) allItems.filter { it.category == cat } else allItems
        updateIngredientCounts(categoryScoped)

        // Apply search on top of category scope
        var filtered = if (query.isNotEmpty()) {
            categoryScoped.filter { it.name.contains(query, ignoreCase = true) }
        } else categoryScoped

        // Apply status filter
        filtered = when (mode) {
            FilterMode.AVAILABLE_ONLY -> filtered.filter { it.isAvailable }
            FilterMode.OUT_OF_STOCK -> filtered.filter { !it.isAvailable }
            FilterMode.LOW_STOCK -> filtered.filter { isLowStock(it) && it.isAvailable }
            FilterMode.ALL -> filtered
        }

        filtered = filtered.sortedWith(compareBy<Ingredient> { it.category }.thenBy { it.name })

        _emptyStateVariant.value = if (filtered.isEmpty()) {
            when {
                allItems.isEmpty() -> EmptyStateVariant.NoIngredients
                query.isNotEmpty() -> EmptyStateVariant.NoSearchResults(query)
                cat != null -> EmptyStateVariant.NoCategoryResults(cat)
                else -> EmptyStateVariant.NoFilterResults(mode)
            }
        } else null

        val grouped = filtered.groupBy { it.category }
        val items = mutableListOf<IngredientListItem>()
        for ((category, groupItems) in grouped) {
            items.add(IngredientListItem.Header(category, groupItems.size))
            for (ingredient in groupItems) {
                items.add(IngredientListItem.Item(ingredient, isLowStock(ingredient)))
            }
        }

        ingredients.value = items
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterMode(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
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
