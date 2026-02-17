package com.example.mealzy.ui.home

import com.example.mealzy.data.model.Recipe

data class RecipeWithIngredientMatch(
    val recipe: Recipe,
    val matchingIngredientCount: Int,
    val totalIngredientCount: Int
) {
    val matchPercentage: Int
        get() = if (totalIngredientCount > 0) {
            (matchingIngredientCount * 100) / totalIngredientCount
        } else 0
}
