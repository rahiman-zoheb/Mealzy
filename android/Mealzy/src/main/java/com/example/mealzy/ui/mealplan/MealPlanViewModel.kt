package com.example.mealzy.ui.mealplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealzy.data.model.MealPlan

class MealPlanViewModel : ViewModel() {

    private val _weeklyMealPlan = MutableLiveData<List<MealPlan>>()
    val weeklyMealPlan: LiveData<List<MealPlan>> = _weeklyMealPlan

    init {
        loadMealPlan()
    }

    private fun loadMealPlan() {
        // TODO: Load from repository
        _weeklyMealPlan.value = emptyList()
    }
}