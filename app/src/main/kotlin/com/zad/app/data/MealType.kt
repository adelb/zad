package com.zad.app.data

import androidx.annotation.StringRes
import com.zad.app.R

enum class MealType(@StringRes val labelRes: Int) {
    BREAKFAST(R.string.meal_breakfast),
    LUNCH(R.string.meal_lunch),
    DINNER(R.string.meal_dinner),
    SNACK(R.string.meal_snack)
}
