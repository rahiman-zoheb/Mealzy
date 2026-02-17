package com.example.mealzy.ui.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.model.Ingredient
import kotlinx.coroutines.launch

enum class FilterMode {
    ALL,
    AVAILABLE_ONLY,
    OUT_OF_STOCK
}

class IngredientsViewModel : ViewModel() {

    private val _allIngredients = MutableLiveData<List<Ingredient>>()

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filterMode = MutableLiveData<FilterMode>(FilterMode.ALL)
    val filterMode: LiveData<FilterMode> = _filterMode

    // Filtered and grouped ingredients
    val ingredients = MediatorLiveData<List<Ingredient>>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadIngredients()
        setupFiltering()
    }

    private fun setupFiltering() {
        ingredients.addSource(_allIngredients) {
            updateFilteredIngredients()
        }
        ingredients.addSource(_searchQuery) {
            updateFilteredIngredients()
        }
        ingredients.addSource(_filterMode) {
            updateFilteredIngredients()
        }
    }

    private fun updateFilteredIngredients() {
        val allItems = _allIngredients.value ?: emptyList()
        val query = _searchQuery.value ?: ""
        val mode = _filterMode.value ?: FilterMode.ALL

        var filtered = allItems

        // Apply search filter
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        // Apply availability filter
        filtered = when (mode) {
            FilterMode.AVAILABLE_ONLY -> filtered.filter { it.isAvailable }
            FilterMode.OUT_OF_STOCK -> filtered.filter { !it.isAvailable }
            FilterMode.ALL -> filtered
        }

        // Sort by category, then by name
        filtered = filtered.sortedWith(
            compareBy<Ingredient> { it.category }
                .thenBy { it.name }
        )

        ingredients.value = filtered
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterMode(mode: FilterMode) {
        _filterMode.value = mode
    }

    private fun loadIngredients() {
        _isLoading.value = true
        viewModelScope.launch {
            // TODO: Load from repository
            // For now, create sample data
            _allIngredients.value = createSampleIngredients()
            _isLoading.value = false
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Save to repository
            val newIngredient = ingredient.copy(id = System.currentTimeMillis())
            val currentList = _allIngredients.value?.toMutableList() ?: mutableListOf()
            currentList.add(newIngredient)
            _allIngredients.value = currentList
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Update in repository
            val currentList = _allIngredients.value?.toMutableList() ?: return@launch
            val index = currentList.indexOfFirst { it.id == ingredient.id }
            if (index != -1) {
                currentList[index] = ingredient
                _allIngredients.value = currentList
            }
        }
    }

    fun toggleIngredientAvailability(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Update in repository
            val currentList = _allIngredients.value?.toMutableList() ?: return@launch
            val index = currentList.indexOfFirst { it.id == ingredient.id }
            if (index != -1) {
                currentList[index] = ingredient.copy(isAvailable = !ingredient.isAvailable)
                _allIngredients.value = currentList
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Delete from repository
            val currentList = _allIngredients.value?.toMutableList() ?: return@launch
            currentList.removeAll { it.id == ingredient.id }
            _allIngredients.value = currentList
        }
    }

    private fun createSampleIngredients(): List<Ingredient> {
        return listOf(
            Ingredient(
                id = 1,
                name = "Chicken Breast",
                quantity = "2",
                unit = "lbs",
                category = "Protein",
                isAvailable = true
            ),
            Ingredient(
                id = 2,
                name = "Onions",
                quantity = "3",
                unit = "pieces",
                category = "Vegetables",
                isAvailable = true
            ),
            Ingredient(
                id = 3,
                name = "Tomatoes",
                quantity = "4",
                unit = "pieces",
                category = "Vegetables",
                isAvailable = false
            ),
            Ingredient(
                id = 4,
                name = "Rice",
                quantity = "2",
                unit = "cups",
                category = "Grains",
                isAvailable = true
            ),
            Ingredient(
                id = 5,
                name = "Olive Oil",
                quantity = "1",
                unit = "bottle",
                category = "Condiments",
                isAvailable = true
            ),
            Ingredient(
                id = 6,
                name = "Eggs",
                quantity = "12",
                unit = "pieces",
                category = "Protein",
                isAvailable = false
            )
        )
    }
}