package com.example.mealzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val unit: String,
    val category: String = "Other",
    val isAvailable: Boolean = true
)