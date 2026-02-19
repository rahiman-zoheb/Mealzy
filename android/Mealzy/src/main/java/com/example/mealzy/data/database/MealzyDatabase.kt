package com.example.mealzy.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mealzy.data.dao.IngredientDao
import com.example.mealzy.data.dao.RecipeDao
import com.example.mealzy.data.dao.RecipeIngredientDao
import com.example.mealzy.data.dao.MealPlanDao
import com.example.mealzy.data.model.*
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Ingredient::class,
        Recipe::class,
        RecipeIngredient::class,
        MealPlan::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MealzyDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
    abstract fun mealPlanDao(): MealPlanDao

    companion object {
        @Volatile
        private var INSTANCE: MealzyDatabase? = null

        fun getDatabase(context: Context): MealzyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealzyDatabase::class.java,
                    "mealzy_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedDatabase(it) }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(db: MealzyDatabase) {
            val ingredientDao = db.ingredientDao()
            val recipeDao = db.recipeDao()
            val recipeIngredientDao = db.recipeIngredientDao()

            // Insert ingredients
            val eggId = ingredientDao.insertIngredient(
                Ingredient(name = "Eggs", quantity = "12", unit = "pieces", category = "Protein", isAvailable = true)
            )
            val milkId = ingredientDao.insertIngredient(
                Ingredient(name = "Milk", quantity = "1", unit = "litre", category = "Dairy", isAvailable = true)
            )
            val butterId = ingredientDao.insertIngredient(
                Ingredient(name = "Butter", quantity = "200", unit = "grams", category = "Dairy", isAvailable = true)
            )
            val chickenId = ingredientDao.insertIngredient(
                Ingredient(name = "Chicken Breast", quantity = "2", unit = "lbs", category = "Protein", isAvailable = true)
            )
            val onionId = ingredientDao.insertIngredient(
                Ingredient(name = "Onions", quantity = "3", unit = "pieces", category = "Vegetables", isAvailable = true)
            )
            val tomatoId = ingredientDao.insertIngredient(
                Ingredient(name = "Tomatoes", quantity = "4", unit = "pieces", category = "Vegetables", isAvailable = false)
            )
            val riceId = ingredientDao.insertIngredient(
                Ingredient(name = "Rice", quantity = "2", unit = "cups", category = "Grains", isAvailable = true)
            )
            val oliveOilId = ingredientDao.insertIngredient(
                Ingredient(name = "Olive Oil", quantity = "1", unit = "bottle", category = "Condiments", isAvailable = true)
            )
            val garlicId = ingredientDao.insertIngredient(
                Ingredient(name = "Garlic", quantity = "1", unit = "head", category = "Vegetables", isAvailable = true)
            )
            val pastaId = ingredientDao.insertIngredient(
                Ingredient(name = "Spaghetti", quantity = "500", unit = "grams", category = "Grains", isAvailable = true)
            )
            val groundBeefId = ingredientDao.insertIngredient(
                Ingredient(name = "Ground Beef", quantity = "500", unit = "grams", category = "Protein", isAvailable = false)
            )
            val greekYogurtId = ingredientDao.insertIngredient(
                Ingredient(name = "Greek Yogurt", quantity = "500", unit = "grams", category = "Dairy", isAvailable = true)
            )
            val berriesId = ingredientDao.insertIngredient(
                Ingredient(name = "Mixed Berries", quantity = "200", unit = "grams", category = "Fruits", isAvailable = true)
            )
            val granolaId = ingredientDao.insertIngredient(
                Ingredient(name = "Granola", quantity = "300", unit = "grams", category = "Grains", isAvailable = true)
            )
            val bellPepperId = ingredientDao.insertIngredient(
                Ingredient(name = "Bell Pepper", quantity = "2", unit = "pieces", category = "Vegetables", isAvailable = true)
            )
            val soySauceId = ingredientDao.insertIngredient(
                Ingredient(name = "Soy Sauce", quantity = "1", unit = "bottle", category = "Condiments", isAvailable = true)
            )

            // Insert recipes and link ingredients
            val scrambledEggsId = recipeDao.insertRecipe(
                Recipe(
                    name = "Scrambled Eggs",
                    description = "Classic fluffy scrambled eggs, perfect for a quick breakfast",
                    instructions = "1. Crack eggs into a bowl and whisk with milk\n2. Season with salt and pepper\n3. Melt butter in a non-stick pan over medium-low heat\n4. Pour in egg mixture and gently fold as it cooks\n5. Remove from heat while still slightly runny — residual heat finishes the job",
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 10,
                    servings = 2,
                    difficulty = "Easy",
                    mealType = MealType.BREAKFAST
                )
            )
            recipeIngredientDao.insertRecipeIngredients(listOf(
                RecipeIngredient(scrambledEggsId, eggId, "4", "pieces"),
                RecipeIngredient(scrambledEggsId, milkId, "2", "tbsp"),
                RecipeIngredient(scrambledEggsId, butterId, "1", "tbsp")
            ))

            val chickenStirFryId = recipeDao.insertRecipe(
                Recipe(
                    name = "Chicken Stir Fry",
                    description = "Quick and healthy chicken stir fry with colourful vegetables",
                    instructions = "1. Cut chicken breast into strips\n2. Heat oil in a wok over high heat\n3. Stir fry chicken until golden, about 5 minutes\n4. Add garlic, onion and bell pepper\n5. Pour in soy sauce and stir fry for another 3 minutes\n6. Serve over steamed rice",
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 20,
                    servings = 4,
                    difficulty = "Medium",
                    mealType = MealType.LUNCH
                )
            )
            recipeIngredientDao.insertRecipeIngredients(listOf(
                RecipeIngredient(chickenStirFryId, chickenId, "500", "grams"),
                RecipeIngredient(chickenStirFryId, onionId, "1", "piece"),
                RecipeIngredient(chickenStirFryId, bellPepperId, "1", "piece"),
                RecipeIngredient(chickenStirFryId, garlicId, "3", "cloves"),
                RecipeIngredient(chickenStirFryId, soySauceId, "3", "tbsp"),
                RecipeIngredient(chickenStirFryId, oliveOilId, "2", "tbsp"),
                RecipeIngredient(chickenStirFryId, riceId, "1", "cup")
            ))

            val spaghettiId = recipeDao.insertRecipe(
                Recipe(
                    name = "Spaghetti Bolognese",
                    description = "Traditional Italian pasta with a rich meat sauce",
                    instructions = "1. Cook spaghetti according to package instructions\n2. Sauté onion and garlic in olive oil\n3. Add ground beef and brown well\n4. Add tomatoes and simmer for 20 minutes\n5. Season with salt, pepper and herbs\n6. Toss pasta with sauce and serve",
                    prepTimeMinutes = 20,
                    cookTimeMinutes = 45,
                    servings = 4,
                    difficulty = "Medium",
                    mealType = MealType.DINNER
                )
            )
            recipeIngredientDao.insertRecipeIngredients(listOf(
                RecipeIngredient(spaghettiId, pastaId, "500", "grams"),
                RecipeIngredient(spaghettiId, groundBeefId, "500", "grams"),
                RecipeIngredient(spaghettiId, tomatoId, "4", "pieces"),
                RecipeIngredient(spaghettiId, onionId, "1", "piece"),
                RecipeIngredient(spaghettiId, garlicId, "3", "cloves"),
                RecipeIngredient(spaghettiId, oliveOilId, "2", "tbsp")
            ))

            val parfaitId = recipeDao.insertRecipe(
                Recipe(
                    name = "Greek Yogurt Parfait",
                    description = "Healthy layered yogurt with fresh berries and crunchy granola",
                    instructions = "1. Spoon half the yogurt into a glass\n2. Add a layer of mixed berries\n3. Sprinkle with granola\n4. Repeat layers\n5. Top with a few extra berries and serve immediately",
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 0,
                    servings = 1,
                    difficulty = "Easy",
                    mealType = MealType.SNACK
                )
            )
            recipeIngredientDao.insertRecipeIngredients(listOf(
                RecipeIngredient(parfaitId, greekYogurtId, "250", "grams"),
                RecipeIngredient(parfaitId, berriesId, "100", "grams"),
                RecipeIngredient(parfaitId, granolaId, "50", "grams")
            ))

            val chickenRiceId = recipeDao.insertRecipe(
                Recipe(
                    name = "Chicken and Rice",
                    description = "Simple one-pan chicken with seasoned rice",
                    instructions = "1. Season chicken with salt and pepper\n2. Brown chicken in olive oil, set aside\n3. Sauté onion and garlic in same pan\n4. Add rice and toast for 1 minute\n5. Add broth and nestle chicken on top\n6. Cover and cook 20 minutes until rice is tender",
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 30,
                    servings = 3,
                    difficulty = "Easy",
                    mealType = MealType.DINNER
                )
            )
            recipeIngredientDao.insertRecipeIngredients(listOf(
                RecipeIngredient(chickenRiceId, chickenId, "400", "grams"),
                RecipeIngredient(chickenRiceId, riceId, "1.5", "cups"),
                RecipeIngredient(chickenRiceId, onionId, "1", "piece"),
                RecipeIngredient(chickenRiceId, garlicId, "2", "cloves"),
                RecipeIngredient(chickenRiceId, oliveOilId, "2", "tbsp")
            ))
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMealType(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun toMealType(mealType: String): MealType {
        return MealType.valueOf(mealType)
    }
}
