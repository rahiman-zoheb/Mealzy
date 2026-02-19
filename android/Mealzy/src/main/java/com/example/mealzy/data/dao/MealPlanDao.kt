package com.example.mealzy.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import java.util.Date

@Dao
interface MealPlanDao {
    
    @Query("SELECT * FROM meal_plans ORDER BY date, mealType")
    fun getAllMealPlans(): LiveData<List<MealPlan>>
    
    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date, mealType")
    fun getMealPlansInRange(startDate: Date, endDate: Date): LiveData<List<MealPlan>>
    
    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanById(id: Long): MealPlan?

    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :dayStart AND :dayEnd AND mealType = :mealType LIMIT 1")
    suspend fun getMealPlanByDateAndType(dayStart: Date, dayEnd: Date, mealType: MealType): MealPlan?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlans(mealPlans: List<MealPlan>)
    
    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)
    
    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
    
    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteMealPlanById(id: Long)
}