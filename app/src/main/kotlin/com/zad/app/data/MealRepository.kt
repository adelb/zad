package com.zad.app.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MealRepository(private val dao: MealDao) {

    fun todayKey(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    suspend fun addEntry(
        mealType: MealType,
        dishId: String,
        dishNameAr: String,
        grams: Int,
        calories: Int,
        photoPath: String?
    ): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            MealEntry(
                dayKey = todayKey(),
                timestampMs = now,
                mealType = mealType,
                dishId = dishId,
                dishNameAr = dishNameAr,
                grams = grams,
                calories = calories,
                photoPath = photoPath
            )
        )
    }

    suspend fun delete(entry: MealEntry) = dao.delete(entry)
    fun entriesToday(): Flow<List<MealEntry>> = dao.entriesForDay(todayKey())
    fun totalToday(): Flow<Int> = dao.totalForDay(todayKey())
    fun perMealToday(): Flow<List<MealTotal>> = dao.totalsPerMeal(todayKey())
    fun recent(): Flow<List<MealEntry>> = dao.recent()
}
