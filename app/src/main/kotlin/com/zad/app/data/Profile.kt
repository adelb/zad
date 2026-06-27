package com.zad.app.data

import androidx.annotation.StringRes
import com.zad.app.R
import java.time.LocalDate
import java.time.Period

enum class Sex(@StringRes val labelRes: Int) {
    MALE(R.string.sex_male),
    FEMALE(R.string.sex_female)
}

enum class ActivityLevel(
    val multiplier: Double,
    @StringRes val labelRes: Int,
    @StringRes val descRes: Int
) {
    SEDENTARY(1.20,  R.string.activity_sedentary,  R.string.activity_sedentary_desc),
    LIGHT    (1.375, R.string.activity_light,      R.string.activity_light_desc),
    MODERATE (1.55,  R.string.activity_moderate,   R.string.activity_moderate_desc),
    HEAVY    (1.725, R.string.activity_heavy,      R.string.activity_heavy_desc),
    VERY_HEAVY(1.90, R.string.activity_very_heavy, R.string.activity_very_heavy_desc)
}

enum class Goal(@StringRes val labelRes: Int, val kcalAdjust: Int) {
    LOSE    (R.string.goal_lose,     -500),
    MAINTAIN(R.string.goal_maintain,    0),
    GAIN    (R.string.goal_gain,     +400)
}

data class Profile(
    val birthDate: LocalDate,
    val sex: Sex,
    val heightCm: Int,
    val weightKg: Double,
    val activity: ActivityLevel,
    val goal: Goal
) {
    val ageYears: Int get() = Period.between(birthDate, LocalDate.now()).years

    val bmr: Int get() {
        val base = 10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears
        return (base + if (sex == Sex.MALE) 5.0 else -161.0).toInt()
    }
    val tdee: Int get() = (bmr * activity.multiplier).toInt()
    val dailyTargetKcal: Int get() = (tdee + goal.kcalAdjust).coerceAtLeast(1200)

    val bmi: Double get() {
        val m = heightCm / 100.0
        return if (m > 0) weightKg / (m * m) else 0.0
    }

    val bmiCategory: BmiCategory get() = when {
        bmi < 18.5 -> BmiCategory.UNDERWEIGHT
        bmi < 25.0 -> BmiCategory.NORMAL
        bmi < 30.0 -> BmiCategory.OVERWEIGHT
        else       -> BmiCategory.OBESE
    }

    /** Recommended daily water intake in ml — 35 ml × kg, rounded to nearest 250. */
    val dailyWaterMl: Int get() {
        val raw = (weightKg * 35).toInt()
        return ((raw + 125) / 250) * 250
    }
}

enum class BmiCategory(@StringRes val labelRes: Int) {
    UNDERWEIGHT(R.string.bmi_underweight),
    NORMAL(R.string.bmi_normal),
    OVERWEIGHT(R.string.bmi_overweight),
    OBESE(R.string.bmi_obese)
}
