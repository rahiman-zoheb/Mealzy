# Mealzy - Meal Preparation Planning App

Mealzy is an Android application designed to help users plan and organize their meal preparation. Built with **Material Design 3** and modern Android development practices, the app provides a beautiful and intuitive interface for managing ingredients, discovering recipes, and creating meal plans.

## Features

### Home Screen
- **Time-based greeting** — Good Morning / Afternoon / Evening based on device time
- **Statistics dashboard** — Live counts for ingredients, recipes, and weekly meals; each card navigates to the corresponding screen
- **Quick action cards** — Add Ingredient, Browse Recipes, Plan Meals
- **Upcoming Meals** — Next 3 days of planned meals
- **Suggested Recipes** — Horizontal carousel showing recipes where you have ≥70% of required ingredients, sorted by best match, updated in real-time as ingredients change

### Recipes
- **Visual recipe cards** with meal-type gradient placeholders (breakfast, lunch, dinner, snack)
- **Real-time search** by name or description
- **Chip filters** by meal type (All, Breakfast, Lunch, Dinner, Snack)
- **Favourite toggle** — heart icon on each card
- **Recipe detail sheet** — shows prep/cook times, servings, difficulty, and full ingredient list with availability status

### Ingredients
- **Real-time search** with filter chips (All, Available Only, Out of Stock)
- **Category grouping** with section headers (Protein, Vegetables, Fruits, Grains, Dairy, Condiments, Spices)
- **Category color strip** on left edge of each card
- **Low-stock warning badge** when quantity is minimal
- **Availability toggle** per ingredient
- **Swipe to delete** with undo snackbar

### Meal Plan
- **Weekly calendar** — 7-day horizontal scroll with Breakfast / Lunch / Dinner / Snack slots per day
- **Week navigation** — Previous / Next / Today buttons; week range shown (e.g. "Week of Feb 10 – 16, 2026")
- **Current day highlighted** with primary color
- **Add meals** by tapping empty slots; tap a planned meal to view options
- **Extended FAB** that shrinks on scroll down and re-extends on scroll up

## Technical Architecture

**Pattern:** MVVM with Repository · Single-Activity with Navigation Component · No DI framework

**Stack:** Room 2.6.1 · Navigation Component 2.7.6 · Material Design 3 · View Binding + Data Binding · Lifecycle/ViewModel 2.7.0 · Kotlin Coroutines

### Data Layer
- **Room Database** (SQLite) with 4 entities: `Ingredient`, `Recipe`, `RecipeIngredient` (join table), `MealPlan`
- **Repository pattern** — `MealzyRepository` is the single source of truth
- **DAOs** for each entity with LiveData queries and suspend functions for writes
- **Type converters** for `Date ↔ Long` and `MealType ↔ String`
- **Auto-seeding** on first install with 16 sample ingredients and 5 sample recipes

### UI Layer
- **MVVM** — each screen has a paired ViewModel exposing LiveData
- **MediatorLiveData** used throughout to combine search + filter streams reactively
- **Coroutines** — all database writes and the recipe-suggestion algorithm run on `viewModelScope`
- **ListAdapter + DiffUtil** on all RecyclerViews for efficient, animated updates
- **Full dark mode** support via `values-night/` resources

### Interactions & Animations (Phase 3)
- Fragment enter/exit transitions and shared-element animations
- List item slide-in animations
- FAB scroll behavior (shrink on scroll down, extend on scroll up)
- Swipe gestures on the ingredients list (delete with undo)
- Haptic feedback on buttons, toggles, and swipe actions

## Project Structure

```
android/Mealzy/src/main/java/com/example/mealzy/
├── data/
│   ├── model/       # Room entities + RecipeIngredientDetail projection
│   ├── dao/         # IngredientDao, RecipeDao, RecipeIngredientDao, MealPlanDao
│   ├── database/    # MealzyDatabase singleton + TypeConverters + seed callback
│   └── repository/  # MealzyRepository
└── ui/
    ├── home/        # HomeFragment, HomeViewModel, SuggestedRecipesAdapter, UpcomingMealsAdapter
    ├── recipes/     # RecipesFragment, RecipesViewModel, RecipeDetailBottomSheet
    ├── ingredients/ # IngredientsFragment, IngredientsViewModel, IngredientsAdapter
    └── mealplan/    # MealPlanFragment, MealPlanViewModel, CalendarDayAdapter, dialogs
```

## Sample Data

Pre-loaded on first install:

**16 ingredients** across Protein, Vegetables, Fruits, Grains, Dairy, and Condiments categories (Eggs, Milk, Butter, Chicken Breast, Onions, Tomatoes, Rice, Olive Oil, Garlic, Spaghetti, Ground Beef, Greek Yogurt, Mixed Berries, Granola, Bell Pepper, Soy Sauce).

**5 recipes:**
| Recipe | Type | Time | Difficulty |
|---|---|---|---|
| Scrambled Eggs | Breakfast | 15 min | Easy |
| Chicken Stir Fry | Lunch | 35 min | Medium |
| Spaghetti Bolognese | Dinner | 65 min | Medium |
| Greek Yogurt Parfait | Snack | 5 min | Easy |
| Chicken and Rice | Dinner | 40 min | Easy |

## Getting Started

1. Clone the repository
2. Open in Android Studio (Electric Eel or later)
3. Build and run on a device or emulator running API 24+
4. The app pre-loads sample ingredients and recipes on first launch

## Future Enhancements

- Shopping list generation from a meal plan
- Nutritional information tracking
- Photo support for custom recipes
- Recipe import from web / external API
- Meal prep reminders and push notifications
- Export / share meal plans
- Grocery shopping mode with a checklist view
- Skeleton loading screens

---

*Built with Material Design 3 · Phases 1, 2 & 3 complete · February 2026*
