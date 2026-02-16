# Mealzy - Meal Preparation Planning App

Mealzy is an Android application designed to help users plan and organize their meal preparation. The app allows users to manage ingredients, discover recipes, and create meal plans for efficient meal prep.

## Features

### 🏠 Home Screen
- Welcome message with time-based greetings
- Quick action cards for easy navigation:
  - Add Ingredient
  - Browse Recipes  
  - Plan Meals
- Today's meals overview

### 🍳 Recipe Suggestions
- Browse all recipes with search functionality
- Filter recipes by meal type (Breakfast, Lunch, Dinner, Snack)
- Recipe cards showing:
  - Prep and cook times
  - Servings and difficulty level
  - Meal type categorization
- Sample recipes included (Scrambled Eggs, Chicken Stir Fry, Spaghetti Bolognese, Greek Yogurt Parfait)

### 🥕 Ingredients Management
- Add ingredients with details:
  - Name and quantity
  - Unit measurements (pieces, cups, lbs, etc.)
  - Categories (Protein, Vegetables, Fruits, Grains, etc.)
  - Availability status
- Visual ingredient cards with category-based color coding
- Toggle ingredient availability with switches
- Sample ingredients included

### 📅 Meal Plan
- Weekly meal planning interface
- Add meals to specific dates and meal types
- Track meal completion status
- Floating action button for quick meal additions

## Technical Architecture

### Data Layer
- **Room Database** for local data persistence
- **Repository Pattern** for data management
- Entity models for:
  - Ingredients
  - Recipes
  - Recipe-Ingredient relationships
  - Meal Plans

### UI Layer
- **MVVM Architecture** with ViewModels
- **Navigation Component** for fragment navigation
- **Material Design 3** components
- **View Binding** for type-safe view references
- **RecyclerView** with DiffUtil for efficient list updates

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

## Future Enhancements

- Recipe import from web/API
- Shopping list generation
- Nutritional information
- Photo support for recipes
- Meal prep reminders
- Export/share meal plans
- Dark mode support

---

*Built with ❤️ for meal prep enthusiasts*