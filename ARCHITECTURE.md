# Mealzy Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MEALZY ARCHITECTURE                         │
│                  Native Android · Kotlin · MVVM                    │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER (UI)                                           │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                     MainActivity                              │  │
│  │              (Single Activity Host)                            │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │              NavHostFragment                             │  │  │
│  │  │         (Navigation Component)                           │  │  │
│  │  │                                                          │  │  │
│  │  │  ┌────────────┐ ┌────────────┐ ┌──────────────────────┐ │  │  │
│  │  │  │    Home    │ │  Recipes   │ │    Ingredients       │ │  │  │
│  │  │  │  Fragment  │ │  Fragment  │ │     Fragment         │ │  │  │
│  │  │  │           │ │           │ │                      │ │  │  │
│  │  │  │ Quick     │ │ Grid View │ │ List + FAB           │ │  │  │
│  │  │  │ Actions   │ │ Search    │ │ AddIngredientDialog  │ │  │  │
│  │  │  │ Today's   │ │ Filter    │ │ Toggle Availability  │ │  │  │
│  │  │  │ Meals     │ │ by Type   │ │                      │ │  │  │
│  │  │  └─────┬──────┘ └─────┬──────┘ └──────────┬───────────┘ │  │  │
│  │  │        │              │                    │             │  │  │
│  │  │  ┌─────┴──────────────┴────────────────────┴───────────┐ │  │  │
│  │  │  │                 MealPlan Fragment                    │ │  │  │
│  │  │  │            Weekly Planning + FAB                     │ │  │  │
│  │  │  └─────────────────────────────────────────────────────┘ │  │  │
│  │  └──────────────────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │              BottomNavigationView                       │  │  │
│  │  │   [Home]    [Recipes]    [Ingredients]    [Meal Plan]   │  │  │
│  │  └─────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  Adapters: RecipesAdapter, IngredientsAdapter (RecyclerView)        │
│  Binding:  ViewBinding + DataBinding                                │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ observes LiveData
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│  VIEWMODEL LAYER                                                    │
│                                                                     │
│  ┌──────────────┐ ┌────────────────┐ ┌────────────────────────────┐│
│  │ HomeViewModel │ │RecipesViewModel│ │ IngredientsViewModel      ││
│  │              │ │               │ │                            ││
│  │ todayMeals  │ │ allRecipes   │ │ allIngredients             ││
│  │ sampleData  │ │ searchQuery  │ │ insert/toggle/delete       ││
│  └──────────────┘ │ mealTypeFilter│ └────────────────────────────┘│
│                    └────────────────┘                               │
│  ┌──────────────────────────┐                                      │
│  │   MealPlanViewModel      │   All ViewModels extend              │
│  │   weeklyPlan             │   AndroidViewModel                   │
│  └──────────────────────────┘                                      │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ calls suspend functions
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│  DATA LAYER                                                         │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                   MealzyRepository                            │  │
│  │         (Single source of truth abstraction)                  │  │
│  │                                                               │  │
│  │  Ingredients: getAll, insert, update, delete, toggleAvail     │  │
│  │  Recipes:     getAll, getById, search, filterByType           │  │
│  │  MealPlans:   getByDateRange, insert, update, toggleComplete  │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                      │
│         ┌────────────────────┼────────────────────┐                 │
│         ▼                    ▼                    ▼                 │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐        │
│  │IngredientDao │  │  RecipeDao   │  │   MealPlanDao     │        │
│  │              │  │              │  │                   │        │
│  │ @Query       │  │ @Query       │  │ @Query            │        │
│  │ @Insert      │  │ @Insert      │  │ @Insert           │        │
│  │ @Update      │  │ @Update      │  │ @Update           │        │
│  │ @Delete      │  │ @Delete      │  │ @Delete           │        │
│  └──────┬───────┘  └──────┬───────┘  └─────────┬─────────┘        │
│         └────────────────┬─┘                    │                   │
│                          ▼                      │                   │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                   MealzyDatabase                              │  │
│  │               (Room / SQLite)                                 │  │
│  │                                                               │  │
│  │  Entities: Recipe, Ingredient, MealPlan, RecipeIngredient     │  │
│  │  Converters: Date ↔ Long, MealType ↔ String                  │  │
│  │  Version: 1  |  Singleton  |  Destructive migration           │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  DATA MODELS                                                        │
│                                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │   Recipe     │  │  Ingredient  │  │      MealPlan            │  │
│  │──────────────│  │──────────────│  │──────────────────────────│  │
│  │ id (PK)     │  │ id (PK)     │  │ id (PK)                 │  │
│  │ name        │  │ name        │  │ recipeId (FK → Recipe)  │  │
│  │ description │  │ quantity    │  │ date                    │  │
│  │ instructions│  │ unit        │  │ mealType                │  │
│  │ prepTime    │  │ category    │  │ servings                │  │
│  │ cookTime    │  │ isAvailable │  │ isCompleted             │  │
│  │ servings    │  └──────────────┘  └──────────────────────────┘  │
│  │ difficulty  │                                                   │
│  │ mealType    │  ┌──────────────────────────────────────────────┐ │
│  │ imageUrl    │  │       RecipeIngredient (Junction)            │ │
│  │ isFavorite  │  │──────────────────────────────────────────────│ │
│  └──────────────┘  │ recipeId (PK, FK) ←──── Recipe              │ │
│                     │ ingredientId (PK, FK) ← Ingredient          │ │
│                     │ quantity, unit                               │ │
│                     └──────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  NAVIGATION GRAPH                                                   │
│                                                                     │
│             ┌──────────────────┐                                    │
│             │   HomeFragment   │ ◄─── startDestination              │
│             └────┬───┬───┬─────┘                                    │
│       action_   │   │   │  action_                                 │
│     to_recipes  │   │   │  to_meal_plan                            │
│                 │   │   │                                           │
│    ┌────────────┘   │   └───────────────┐                          │
│    ▼                ▼                   ▼                          │
│  ┌──────────┐ ┌──────────────┐ ┌──────────────┐                   │
│  │ Recipes  │ │ Ingredients  │ │  MealPlan    │                   │
│  │ Fragment │ │  Fragment    │ │  Fragment    │                   │
│  └──────────┘ │(showAddDialog│ └──────────────┘                   │
│               │  arg: bool)  │                                     │
│               └──────────────┘                                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  DEPENDENCIES                                                       │
│                                                                     │
│  AndroidX Core KTX 1.12  │  Material 3 (1.11)  │  Room 2.6.1      │
│  AppCompat 1.6.1         │  RecyclerView 1.3.2  │  Navigation 2.7.6│
│  ConstraintLayout 2.1.4  │  CardView 1.0.0      │  Lifecycle 2.7.0 │
│                                                                     │
│  Build: AGP 8.7.3  │  Kotlin 1.9.25  │  SDK 24–34                 │
└─────────────────────────────────────────────────────────────────────┘
```

## Key Architectural Patterns

- **Single Activity** — `MainActivity` hosts all fragments via `NavHostFragment`
- **MVVM** — each screen has a paired ViewModel exposing `LiveData` to the Fragment
- **Repository Pattern** — `MealzyRepository` abstracts all database operations behind a clean API
- **Room Persistence** — 4 entities with DAOs, type converters, and a singleton database
- **Navigation Component** — declarative navigation graph with bottom navigation and safe args
- **No DI framework** — manual instantiation (a candidate for Hilt in the future)