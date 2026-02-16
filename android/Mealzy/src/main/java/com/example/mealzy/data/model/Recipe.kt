package com.example.mealzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val instructions: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val mealType: MealType,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}