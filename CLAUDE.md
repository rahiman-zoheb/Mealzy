# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug           # Build debug APK
./gradlew assembleRelease         # Build release APK
./gradlew test                    # Run unit tests
./gradlew connectedAndroidTest    # Run instrumentation tests (requires device/emulator)
./gradlew clean                   # Clean build
./gradlew lint                    # Run lint checks
```

To run a single test class:
```bash
./gradlew test --tests "com.example.mealzy.ExampleUnitTest"
```

No unit or instrumentation tests have been written yet despite JUnit and Espresso dependencies being present.

## Project Layout

The `:app` module is mapped to `android/Mealzy/` via `settings.gradle.kts`:
```
project(":app").projectDir = file("android/Mealzy")
```

All source code lives under:
```
android/Mealzy/src/main/java/com/example/mealzy/
```

## Architecture Overview

**Pattern:** MVVM with Repository, using LiveData and Kotlin Coroutines (no Hilt/Dagger — no DI framework).

**Stack:** Room 2.6.1 · Navigation Component 2.7.6 · Material Design 3 · View Binding + Data Binding · Lifecycle/ViewModel 2.7.0

### Layer Structure

```
data/
  model/       — Room @Entity classes: Ingredient, Recipe, RecipeIngredient, MealPlan
                 Also contains RecipeIngredientDetail — a non-entity POJO projection used
                 by RecipeIngredientDao JOIN queries (not tracked by Room)
  dao/         — DAO interfaces (suspend functions for writes, LiveData for reads)
  database/    — MealzyDatabase (singleton, version 2) + TypeConverters + seeding callback
  repository/  — MealzyRepository: single source of truth, constructed directly in each ViewModel

ui/
  home/        — HomeFragment + HomeViewModel (greeting, stats, upcoming meals, recipe suggestions)
  recipes/     — RecipesFragment + RecipesViewModel + RecipeDetailBottomSheet + RecipeDetailViewModel
  ingredients/ — IngredientsFragment + IngredientsViewModel (swipe-to-delete with undo, search, filter)
  mealplan/    — MealPlanFragment + MealPlanViewModel (weekly calendar, week navigation)
```

### Key Architectural Details

- **No DI framework:** Each ViewModel extends `AndroidViewModel(application)` and constructs `MealzyDatabase.getDatabase(application)` and `MealzyRepository(...)` directly inside `init {}`. There is no shared ViewModel factory or injection.
- **`fallbackToDestructiveMigration()`:** The database is configured with this option, meaning any Room schema change (e.g. bumping `version` in `@Database`) will **wipe all user data**. Migrations must be implemented explicitly to avoid this.
- **Database seeding:** `MealzyDatabase.getDatabase()` registers a `RoomDatabase.Callback` that populates 16 ingredients and 5 recipes on first creation only (`onCreate`).
- **MediatorLiveData:** Used extensively in ViewModels to combine search/filter streams (e.g., `RecipesViewModel` merges a search query + meal type filter chip into a single observable list; `IngredientsViewModel` does the same for search + `FilterMode` enum).
- **Recipe suggestion algorithm:** `HomeViewModel.calculateRecipeMatches()` fetches all `RecipeIngredient` rows for each recipe in parallel (`async`/`awaitAll`), counts how many required ingredient IDs are in the available set, and surfaces recipes with ≥70% match (top 10, sorted by descending match %).
- **Navigation:** Single-Activity (`MainActivity`) with `NavHostFragment` and a bottom navigation bar wired to a navigation graph (`res/navigation/`). `IngredientsFragment` accepts a `showAddDialog: Boolean` navigation argument used by the Home screen "Add Ingredient" quick-action card.
- **Theming:** Full Material Design 3 color system with light (`res/values/`) and dark (`res/values-night/`) variants. Recipe cards use gradient drawables keyed by the `MealType` enum (`BREAKFAST`, `LUNCH`, `DINNER`, `SNACK`).

### Data Flow

```
Fragment (observes LiveData) ← ViewModel (transforms via MediatorLiveData) ← Repository ← DAO ← Room DB
         ↓ user actions
Fragment → ViewModel (viewModelScope.launch) → Repository (suspend fun) → DAO
```

### RecipeIngredient Join Table

`RecipeIngredient` is a many-to-many join between `Recipe` and `Ingredient` with a composite primary key `(recipeId, ingredientId)` and CASCADE deletes on both foreign keys. `RecipeIngredientDao` has specialised queries like `getIngredientsForRecipeSync()` (used by the suggestion algorithm as a `suspend fun`) and `getIngredientDetailsForRecipe()` which returns `RecipeIngredientDetail` projections including availability and category.
