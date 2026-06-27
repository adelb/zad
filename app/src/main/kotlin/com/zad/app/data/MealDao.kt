package com.zad.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class MealTotal(val mealType: MealType, val totalCalories: Int)

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MealEntry): Long

    @Delete suspend fun delete(entry: MealEntry)

    @Query("SELECT * FROM meal_entries WHERE dayKey = :dayKey ORDER BY timestampMs ASC")
    fun entriesForDay(dayKey: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries ORDER BY timestampMs DESC LIMIT :limit")
    fun recent(limit: Int = 200): Flow<List<MealEntry>>

    @Query("SELECT IFNULL(SUM(calories),0) FROM meal_entries WHERE dayKey = :dayKey")
    fun totalForDay(dayKey: String): Flow<Int>

    @Query("SELECT mealType, IFNULL(SUM(calories),0) AS totalCalories FROM meal_entries WHERE dayKey = :dayKey GROUP BY mealType")
    fun totalsPerMeal(dayKey: String): Flow<List<MealTotal>>
}
