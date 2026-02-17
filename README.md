# Mealzy - Meal Preparation Planning App

Mealzy is an Android application designed to help users plan and organize their meal preparation. Built with **Material Design 3** and modern Android development practices, the app provides a beautiful and intuitive interface for managing ingredients, discovering recipes, and creating meal plans.

## ✨ Features

### 🏠 Home Screen
- **Time-based greetings** - Dynamic welcome message (Good Morning/Afternoon/Evening)
- **Statistics dashboard** - Live counts with clickable cards:
  - Ingredients count (navigates to Ingredients screen)
  - Recipes count (navigates to Recipes screen)
  - Weekly meals count (navigates to Meal Plan screen)
- **Quick action cards** - Easy navigation with modern outline-style cards:
  - Add Ingredient (green icon)
  - Browse Recipes (orange icon)
  - Plan Meals (blue icon)
- **Upcoming Meals** - View next 3 days of planned meals
- **Suggested Recipes** - Smart horizontal carousel showing recipes with 70%+ ingredient match:
  - Real-time ingredient matching using RecipeIngredient database
  - Shows X/Y ingredients available for each recipe
  - Sorted by best match percentage
  - Updates automatically when ingredients change
  - Displays top 10 suggestions

### 🍳 Recipe Suggestions
- **Visual recipe cards** with meal-type gradient placeholders:
  - Breakfast: Warm yellow-orange gradient with sunrise icon
  - Lunch: Green gradient with bowl icon
  - Dinner: Blue-purple gradient with dinner plate icon
  - Snack: Pink-orange gradient with snack icon
- **Search functionality** - Real-time recipe search
- **Chip filters** - Filter by meal type (All, Breakfast, Lunch, Dinner, Snack)
- **Favorite recipes** - Heart icon to mark favorite recipes
- **Recipe details** showing:
  - Prep and cook times
  - Servings and difficulty level
  - Meal type categorization
- Sample recipes included (Scrambled Eggs, Chicken Stir Fry, Spaghetti Bolognese, Greek Yogurt Parfait)

### 🥕 Ingredients Management
- **Search & filter** - Find ingredients quickly with real-time search
- **Filter chips** - View all, available only, or out of stock ingredients
- **Category grouping** - Ingredients organized by category with section headers
- **Visual indicators**:
  - Category color strip on left edge (4dp width)
  - Low-stock warning badge (yellow "!" icon)
  - Availability status with toggle switch
- **Add ingredients** with details:
  - Name and quantity
  - Unit measurements (pieces, cups, lbs, kg, oz, etc.)
  - Categories (Protein, Vegetables, Fruits, Grains, Dairy, Condiments, Spices)
  - Availability status
- **Swipe to delete** - Easy ingredient removal with undo option
- Modern card design with proper elevation and theme colors

### 📅 Meal Plan
- **Weekly calendar view** - Fully functional horizontal scroll through 7 day columns
- **Week navigation** - Previous/Next buttons and "Today" quick jump
  - Week range display (e.g., "Week of Feb 10 - 16, 2026")
  - Smooth week-by-week navigation
- **Daily meal slots** for each day:
  - Breakfast (yellow/warm color label)
  - Lunch (green color label)
  - Dinner (blue color label)
  - Snack (orange color label)
- **Visual day cards** - Material Design cards with:
  - Day name (MON, TUE, etc.) and day number
  - Current day highlighted with primary color
  - All 4 meal slots in scrollable view
- **Empty meal slots** - Display "Add Meal" button with plus icon
- **Planned meals** - Show recipe name and time in colored cards
- **Interactive slots** - Click empty slots to add meals, click meals to view details
- Extended FAB with "Add Meal" text

## Technical Architecture

### Data Layer
- **Room Database** for local data persistence with 4 entities:
  - Ingredients
  - Recipes
  - RecipeIngredient (join table with foreign keys)
  - MealPlans
- **Repository Pattern** for data management
- **DAOs** for database operations:
  - IngredientDao - CRUD + category filtering + availability queries
  - RecipeDao - CRUD + meal type filtering + favorites + search
  - RecipeIngredientDao - Recipe-ingredient relationships + availability matching
  - MealPlanDao - CRUD + date range queries
- **Type Converters** for Date and MealType enums

### UI Layer
- **MVVM Architecture** with AndroidViewModel
  - HomeViewModel - Stats calculation, ingredient matching, upcoming meals
  - RecipesViewModel - Recipe filtering, search, favorites
  - IngredientsViewModel - Search, filtering, category grouping with MediatorLiveData
  - MealPlanViewModel - Weekly calendar logic, week navigation, meal organization
- **Navigation Component** for fragment navigation
- **Material Design 3** - Complete implementation with:
  - Dynamic color theming (primary, secondary, tertiary)
  - Dark mode support with night resources
  - Typography scale and hierarchy
  - Elevation system with tonal surfaces
- **View Binding** for type-safe view references
- **RecyclerView** with DiffUtil and ListAdapter for efficient list updates:
  - RecipesAdapter - Gradient backgrounds, favorite toggling
  - IngredientsAdapter - Category colors, low-stock indicators
  - SuggestedRecipesAdapter - Horizontal carousel
  - CalendarDayAdapter - Weekly calendar with dynamic meal slots
- **Modern card designs** - 16dp radius, proper elevation, theme-aware colors
- **Coroutines** - Async data processing with viewModelScope
- **LiveData Transformations** - map, switchMap, MediatorLiveData for reactive data

### Key Components
- Bottom Navigation for main screen switching
- Floating Action Buttons for primary actions
- Dialog fragments for data input
- Custom adapters for RecyclerView lists
- Search functionality with real-time filtering

## Project Structure

```
android/Mealzy/
├── src/main/java/com/example/mealzy/
│   ├── data/
│   │   ├── model/          # Data models (Ingredient, Recipe, MealPlan)
│   │   ├── dao/            # Room DAOs for database operations
│   │   ├── database/       # Database setup and type converters
│   │   └── repository/     # Data repository layer
│   ├── ui/
│   │   ├── home/          # Home fragment and ViewModel
│   │   ├── recipes/       # Recipe browsing and search
│   │   ├── ingredients/   # Ingredient management
│   │   └── mealplan/      # Meal planning interface
│   └── MainActivity.kt     # Main activity with navigation
├── src/main/res/
│   ├── layout/            # XML layouts
│   ├── drawable/          # Vector icons and backgrounds
│   ├── values/            # Strings, colors, themes
│   ├── menu/              # Bottom navigation menu
│   └── navigation/        # Navigation graph
└── build.gradle.kts       # Module dependencies
```

## Sample Data

The app comes with sample data to demonstrate functionality:

### Ingredients
- Chicken Breast (2 lbs, Protein)
- Onions (3 pieces, Vegetables)
- Tomatoes (4 pieces, Vegetables) 
- Rice (2 cups, Grains)
- Olive Oil (1 bottle, Condiments)
- Eggs (12 pieces, Protein)

### Recipes
- **Scrambled Eggs** (Breakfast, 15min total, Easy)
- **Chicken Stir Fry** (Lunch, 35min total, Medium)
- **Spaghetti Bolognese** (Dinner, 65min total, Medium)
- **Greek Yogurt Parfait** (Snack, 5min total, Easy)

## Dependencies

- AndroidX Core KTX
- Material Design Components
- Navigation Component
- Room Database
- RecyclerView
- Lifecycle Components
- View Binding & Data Binding

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Build and run on Android device/emulator (API 24+)
4. Start by adding ingredients using the + button
5. Browse sample recipes in the Recipes tab
6. Create meal plans in the Meal Plan section

## Recent UI/UX Enhancements

### Material Design 3 Implementation (100% Complete)
- ✅ Complete MD3 color system with dark mode support
- ✅ Typography scale and visual hierarchy improvements
- ✅ Modern card designs across all screens
- ✅ Theme-aware colors (`?attr/colorSurface`, etc.)

### Visual Improvements (100% Complete)
- ✅ Recipe gradient placeholders with meal-type icons
- ✅ Ingredient category color strips and low-stock indicators
- ✅ Favorite recipe functionality with heart icon
- ✅ Statistics dashboard on home screen with live data
- ✅ Suggested recipes carousel with real ingredient matching
- ✅ Weekly calendar view with 7-day horizontal scroll
- ✅ Current day highlighting in calendar

### UX Enhancements (100% Complete)
- ✅ Chip filters replacing spinners (better touch targets)
- ✅ Search functionality on recipes and ingredients with real-time filtering
- ✅ Filter chips on ingredients (All, Available, Out of Stock)
- ✅ Weekly calendar view for meal planning
- ✅ Week navigation with prev/next/today buttons
- ✅ Extended FABs with descriptive text labels
- ✅ Clickable stat cards for quick navigation
- ✅ Interactive meal slots (add/view meals)

### Data & Logic Implementation (100% Complete)
- ✅ RecipeIngredientDao for join table queries
- ✅ Real-time ingredient matching algorithm (70%+ threshold)
- ✅ Parallel recipe evaluation with coroutines
- ✅ MediatorLiveData for combined search + filter
- ✅ Calendar day builder with meal organization by type
- ✅ Weekly date range calculation and navigation
- ✅ Stats calculation from repository data

## Future Enhancements

- Recipe import from web/API
- Shopping list generation based on meal plan
- Nutritional information tracking
- Photo support for custom recipes
- Meal prep reminders and notifications
- Export/share meal plans
- Grocery shopping mode with checklist
- Recipe detail view with instructions
- Add/edit meal dialogs for calendar
- Swipe-to-delete with undo on ingredients
- FAB scroll behavior animations
- Skeleton loading screens
- List item animations and transitions

---

*Built with ❤️ for meal prep enthusiasts*
*UI/UX fully enhanced with Material Design 3 - February 2026*
*Phase 1 & 2 Complete: Foundation + All Screen Enhancements Implemented*