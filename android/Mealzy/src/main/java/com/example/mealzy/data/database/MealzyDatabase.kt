package com.example.mealzy.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mealzy.data.dao.IngredientDao
import com.example.mealzy.data.dao.RecipeDao
import com.example.mealzy.data.dao.RecipeIngredientDao
import com.example.mealzy.data.dao.MealPlanDao
import com.example.mealzy.data.model.*
import java.util.Date

@Database(
    entities = [
        Ingredient::class,
        Recipe::class,
        RecipeIngredient::class,
        MealPlan::class
    ],
    version = 1,
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
                .build()
                INSTANCE = instance
                instance
            }
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