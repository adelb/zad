package com.zad.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter fun fromMealType(t: MealType): String = t.name
    @TypeConverter fun toMealType(s: String): MealType = MealType.valueOf(s)
}
