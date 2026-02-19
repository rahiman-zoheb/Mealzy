package com.example.mealzy.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mealzy.data.database.MealzyDatabase
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.data.repository.MealzyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealzyRepository

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> = _welcomeMessage

    // Stats LiveData
    private val _stats = MediatorLiveData<HomeStats>()
    val stats: LiveData<HomeStats> = _stats

    // Upcoming meals (next 3 days) with recipe names
    val upcomingMeals: LiveData<List<MealPlan>>
    val upcomingMealItems: LiveData<List<com.example.mealzy.ui.home.UpcomingMealItem>>

    // Suggested recipes (70%+ ingredient match)
    private val _suggestedRecipes = MediatorLiveData<List<RecipeWithIngredientMatch>>()
    val suggestedRecipes: LiveData<List<RecipeWithIngredientMatch>> = _suggestedRecipes

    init {
        val database = MealzyDatabase.getDatabase(application)
        repository = MealzyRepository(
            database.ingredientDao(),
            database.recipeDao(),
            database.recipeIngredientDao(),
            database.mealPlanDao()
        )

        // Set welcome message
        setWelcomeMessage()

        // Setup stats calculation
        setupStats()

        // Setup upcoming meals (next 3 days)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val threeDaysLater = calendar.time

        val upcomingMealsSource = repository.getMealPlansInRange(today, threeDaysLater)
        upcomingMeals = upcomingMealsSource.map { meals -> meals.sortedBy { it.date }.take(3) }

        val allRecipesForHome = repository.getAllRecipes()
        upcomingMealItems = upcomingMeals.switchMap { meals ->
            allRecipesForHome.map { recipes ->
                val recipeMap = recipes.associateBy { it.id }
                val dateFormat = java.text.SimpleDateFormat("EEE, MMM d", java.util.Locale.getDefault())
                meals.mapNotNull { meal ->
                    val recipe = recipeMap[meal.recipeId] ?: return@mapNotNull null
                    UpcomingMealItem(
                        recipeName = recipe.name,
                        mealType = meal.mealType,
                        dateLabel = dateFormat.format(meal.date)
                    )
                }
            }
        }

        // Setup suggested recipes with real ingredient matching
        setupSuggestedRecipes()
    }

    private fun setupSuggestedRecipes() {
        val availableIngredientsLiveData = repository.getAvailableIngredients()
        val allRecipesLiveData = repository.getAllRecipes()

        _suggestedRecipes.addSource(availableIngredientsLiveData) { availableIngredients ->
            val recipes = allRecipesLiveData.value
            if (recipes != null) {
                calculateRecipeMatches(recipes, availableIngredients)
            }
        }

        _suggestedRecipes.addSource(allRecipesLiveData) { recipes ->
            val availableIngredients = availableIngredientsLiveData.value
            if (availableIngredients != null) {
                calculateRecipeMatches(recipes, availableIngredients)
            }
        }
    }

    private fun setWelcomeMessage() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good Morning!"
            hour < 17 -> "Good Afternoon!"
            else -> "Good Evening!"
        }

        _welcomeMessage.value = greeting
    }

    private fun setupStats() {
        val ingredientsLiveData = repository.getAllIngredients()
        val recipesLiveData = repository.getAllRecipes()

        // Calculate weekly meal count (current week)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val weekEnd = calendar.time

        val weeklyMealsLiveData = repository.getMealPlansInRange(weekStart, weekEnd)

        _stats.addSource(ingredientsLiveData) { ingredients ->
            updateStats(ingredients, recipesLiveData.value, weeklyMealsLiveData.value)
        }

        _stats.addSource(recipesLiveData) { recipes ->
            updateStats(ingredientsLiveData.value, recipes, weeklyMealsLiveData.value)
        }

        _stats.addSource(weeklyMealsLiveData) { meals ->
            updateStats(ingredientsLiveData.value, recipesLiveData.value, meals)
        }
    }

    private fun updateStats(
        ingredients: List<Ingredient>?,
        recipes: List<Recipe>?,
        weeklyMeals: List<MealPlan>?
    ) {
        _stats.value = HomeStats(
            ingredientCount = ingredients?.size ?: 0,
            recipeCount = recipes?.size ?: 0,
            weeklyMealCount = weeklyMeals?.size ?: 0
        )
    }

    private fun calculateRecipeMatches(
        recipes: List<Recipe>,
        availableIngredients: List<Ingredient>
    ) {
        viewModelScope.launch {
            val availableIngredientIds = availableIngredients.map { it.id }.toSet()

            // Calculate matches for all recipes in parallel
            val matches = recipes.map { recipe ->
                async {
                    val recipeIngredients = repository.getIngredientsForRecipeSync(recipe.id)
                    val totalCount = recipeIngredients.size

                    if (totalCount == 0) return@async null

                    val matchingCount = recipeIngredients.count { it.ingredientId in availableIngredientIds }
                    val matchPercentage = (matchingCount * 100) / totalCount

                    if (matchPercentage >= 70) {
                        RecipeWithIngredientMatch(
                            recipe = recipe,
                            matchingIngredientCount = matchingCount,
                            totalIngredientCount = totalCount
                        )
                    } else null
                }
            }.awaitAll().filterNotNull()

            // Sort by match percentage (highest first) and limit to top 10
            _suggestedRecipes.value = matches.sortedByDescending { it.matchPercentage }.take(10)
        }
    }

    fun refreshData() {
        setWelcomeMessage()
    }
}