package com.example.mealzy.ui.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.model.Ingredient
import kotlinx.coroutines.launch

class IngredientsViewModel : ViewModel() {

    private val _ingredients = MutableLiveData<List<Ingredient>>()
    val ingredients: LiveData<List<Ingredient>> = _ingredients

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var allIngredients = mutableListOf<Ingredient>()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        _isLoading.value = true
        viewModelScope.launch {
            // TODO: Load from repository
            // For now, create sample data
            allIngredients = createSampleIngredients().toMutableList()
            _ingredients.value = allIngredients
            _isLoading.value = false
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Save to repository
            val newIngredient = ingredient.copy(id = System.currentTimeMillis())
            allIngredients.add(newIngredient)
            _ingredients.value = allIngredients.toList()
        }
    }

    fun toggleIngredientAvailability(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Update in repository
            val index = allIngredients.indexOfFirst { it.id == ingredient.id }
            if (index != -1) {
                allIngredients[index] = ingredient.copy(isAvailable = !ingredient.isAvailable)
                _ingredients.value = allIngredients.toList()
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // TODO: Delete from repository
            allIngredients.removeAll { it.id == ingredient.id }
            _ingredients.value = allIngredients.toList()
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