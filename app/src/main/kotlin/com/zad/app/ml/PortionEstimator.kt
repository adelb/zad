package com.zad.app.ml

import kotlin.math.max
import kotlin.math.min

object PortionEstimator {

    /**
     * Convert measured plate area (cm²) + dish to grams + calories.
     *
     * grams = area_cm² × thickness_cm × density_g_per_cm³
     * calories = grams × kcal_per_100g / 100
     *
     * Clamped to [20g, 2000g] to absorb noisy taps.
     */
    fun fromArea(dish: Dish, plateAreaCm2: Double): Pair<Int, Int> {
        val volumeCm3 = plateAreaCm2 * dish.typicalThicknessCm
        val rawGrams = volumeCm3 * dish.densityGPerCm3
        val grams = clampGrams(rawGrams.toInt())
        return grams to dish.caloriesFor(grams)
    }

    /** When the user skips scaling, fall back to the dish's typical portion. */
    fun typical(dish: Dish): Pair<Int, Int> =
        dish.typicalGrams to dish.caloriesFor(dish.typicalGrams)

    /** When the user nudges the grams slider — recompute calories. */
    fun forGrams(dish: Dish, grams: Int): Pair<Int, Int> {
        val g = clampGrams(grams)
        return g to dish.caloriesFor(g)
    }

    private fun clampGrams(g: Int): Int = max(20, min(2000, g))
}

/**
 * Helper: pixel area of a rectangle the user drew around the dish,
 * scaled to cm² using the reference-card calibration.
 */
fun pixelAreaToCm2(widthPx: Float, heightPx: Float, mmPerPx: Double): Double {
    val widthCm = widthPx * mmPerPx / 10.0
    val heightCm = heightPx * mmPerPx / 10.0
    return widthCm * heightCm
}
