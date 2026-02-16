package com.example.mealzy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealzy.data.model.MealPlan
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> = _welcomeMessage

    private val _todaysMeals = MutableLiveData<List<MealPlan>>()
    val todaysMeals: LiveData<List<MealPlan>> = _todaysMeals

    private val _quickStats = MutableLiveData<QuickStats>()
    val quickStats: LiveData<QuickStats> = _quickStats

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        // Set welcome message with current time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when {
            hour < 12 -> "Good Morning!"
            hour < 17 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
        
        _welcomeMessage.value = greeting

        // Load today's meals (placeholder for now)
        loadTodaysMeals()

        // Load quick stats
        loadQuickStats()
    }

    private fun loadTodaysMeals() {
        // TODO: Load actual meals from repository
        // For now, using empty list
        _todaysMeals.value = emptyList()
    }

    private fun loadQuickStats() {
        // TODO: Load actual stats from repository
        _quickStats.value = QuickStats(
            totalIngredients = 0,
            totalRecipes = 0,
            mealsThisWeek = 0
        )
    }

    fun refreshData() {
        loadHomeData()
    }
}

data class QuickStats(
    val totalIngredients: Int,
    val totalRecipes: Int,
    val mealsThisWeek: Int
)