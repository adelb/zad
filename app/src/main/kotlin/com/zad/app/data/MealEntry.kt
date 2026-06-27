package com.zad.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One scanned/added portion. dayKey = local ISO date "yyyy-MM-dd" for fast per-day grouping.
 */
@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayKey: String,
    val timestampMs: Long,
    val mealType: MealType,
    val dishId: String,
    val dishNameAr: String,
    val grams: Int,
    val calories: Int,
    val photoPath: String?
)
