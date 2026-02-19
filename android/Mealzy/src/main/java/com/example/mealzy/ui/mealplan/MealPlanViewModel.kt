package com.example.mealzy.ui.mealplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.database.MealzyDatabase
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.data.repository.MealzyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val date: Date,
    val dayName: String,
    val dayNumber: Int,
    val isToday: Boolean,
    val meals: Map<MealType, MealPlan?>,
    val recipeNames: Map<Long, String> = emptyMap()
)

class MealPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository = run {
        val database = MealzyDatabase.getDatabase(application)
        MealzyRepository(
            database.ingredientDao(),
            database.recipeDao(),
            database.recipeIngredientDao(),
            database.mealPlanDao()
        )
    }

    private val _currentWeekStart = MutableLiveData<Date>()
    val currentWeekStart: LiveData<Date> = _currentWeekStart

    // Week range text (e.g., "Week of Feb 10 - 16, 2026")
    val weekRangeText: LiveData<String> = _currentWeekStart.map { weekStart ->
        val calendar = Calendar.getInstance()
        calendar.time = weekStart

        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        val startMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(weekStart)

        // Get end of week (6 days later)
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endDay = calendar.get(Calendar.DAY_OF_MONTH)
        val endMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
        val year = calendar.get(Calendar.YEAR)

        if (startMonth == endMonth) {
            "Week of $startMonth $startDay - $endDay, $year"
        } else {
            "Week of $startMonth $startDay - $endMonth $endDay, $year"
        }
    }

    // All recipes for name lookup and recipe picker
    val allRecipes: LiveData<List<Recipe>> = repository.getAllRecipes()

    // Calendar days (7 days for the current week)
    private val _calendarDays = MediatorLiveData<List<CalendarDay>>()
    val calendarDays: LiveData<List<CalendarDay>> = _calendarDays

    // Tracks the weekly meal plans LiveData so we can swap sources
    private var weeklyMealsSource: LiveData<List<MealPlan>>? = null

    init {
        // Rebuild calendar whenever recipes change (for name display)
        _calendarDays.addSource(allRecipes) { rebuildCalendar() }

        // Initialize to current week (also triggers first calendar build)
        goToToday()
    }

    private fun rebuildCalendar() {
        val weekStart = _currentWeekStart.value ?: return

        // Remove old weekly meals source if present
        weeklyMealsSource?.let { _calendarDays.removeSource(it) }

        val weekEnd = Calendar.getInstance().apply {
            time = weekStart
            add(Calendar.DAY_OF_YEAR, 7)
        }.time

        val newSource = repository.getMealPlansInRange(weekStart, weekEnd)
        weeklyMealsSource = newSource

        _calendarDays.addSource(newSource) { mealPlans ->
            val recipeNames = allRecipes.value?.associate { it.id to it.name } ?: emptyMap()
            _calendarDays.value = buildCalendarDays(weekStart, mealPlans, recipeNames)
        }
    }

    fun addMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply { time = mealPlan.date }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val dayStart = cal.time
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val dayEnd = cal.time

            val existing = repository.getMealPlanByDateAndType(dayStart, dayEnd, mealPlan.mealType)
            if (existing == null) {
                repository.insertMealPlan(mealPlan)
            }
        }
    }

    fun deleteMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.deleteMealPlan(mealPlan)
        }
    }

    private fun buildCalendarDays(
        weekStart: Date,
        mealPlans: List<MealPlan>,
        recipeNames: Map<Long, String> = emptyMap()
    ): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = weekStart

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val dayNames = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

        for (i in 0..6) {
            val dayDate = calendar.time
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-6
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // Check if this is today
            val isToday = isSameDay(calendar, today)

            // Group meals by meal type for this day
            val mealsForDay = mealPlans.filter { isSameDay(it.date, dayDate) }
            val mealsByType = mutableMapOf<MealType, MealPlan?>()
            MealType.values().forEach { type ->
                mealsByType[type] = mealsForDay.find { it.mealType == type }
            }

            days.add(
                CalendarDay(
                    date = dayDate,
                    dayName = dayNames[dayOfWeek],
                    dayNumber = dayOfMonth,
                    isToday = isToday,
                    meals = mealsByType,
                    recipeNames = recipeNames
                )
            )

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return days
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun navigateWeek(offset: Int) {
        val current = _currentWeekStart.value ?: return
        val calendar = Calendar.getInstance()
        calendar.time = current
        calendar.add(Calendar.WEEK_OF_YEAR, offset)
        _currentWeekStart.value = calendar.time
        rebuildCalendar()
    }

    fun goToToday() {
        val calendar = Calendar.getInstance()
        // Set to start of week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        _currentWeekStart.value = calendar.time
        rebuildCalendar()
    }
}