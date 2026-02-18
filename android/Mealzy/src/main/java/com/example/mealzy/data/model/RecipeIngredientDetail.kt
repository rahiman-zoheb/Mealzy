package com.example.mealzy.data.model

data class RecipeIngredientDetail(
    val ingredientId: Long,
    val ingredientName: String,
    val quantity: String,
    val unit: String,
    val isAvailable: Boolean,
    val category: String
)
