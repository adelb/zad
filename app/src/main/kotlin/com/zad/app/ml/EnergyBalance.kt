package com.zad.app.ml

import com.zad.app.data.Profile

/** ~7700 kcal per kg of body fat — used for the day-end weight projection. */
const val KCAL_PER_KG = 7700.0

data class EnergyBalance(
    val consumed: Int,
    val targetKcal: Int,
    val workoutBurn: Int,
    val watchActiveBurn: Int,
    val netIntake: Int,
    val remaining: Int,
    val projectedKgChange: Double,
    val isSurplus: Boolean
)

object EnergyBalanceCalc {

    /**
     * Compute the day's net intake and the projected kg change at the
     * current rate if the rest of the day stays as-is.
     *
     * Logic:
     *  - targetKcal already accounts for the user's chosen goal
     *    (lose / maintain / gain) via Mifflin-St Jeor + activity multiplier.
     *  - workoutBurn is from logged ExerciseSets — extra activity above
     *    the baseline already in TDEE.
     *  - watchActiveBurn is "active calories" from Health Connect — also
     *    extra above baseline. We avoid double-counting the user's normal
     *    movement which is already inside TDEE.
     *
     *  netIntake     = consumed - (workoutBurn + watchActiveBurn)
     *  remaining     = targetKcal - netIntake
     *  surplus       = netIntake - targetKcal       (negative = deficit)
     *  projectedKg   = surplus / 7700
     */
    fun compute(
        profile: Profile?,
        consumed: Int,
        workoutBurn: Int,
        watchActiveBurn: Int
    ): EnergyBalance {
        val target = profile?.dailyTargetKcal ?: 2000
        val extraBurn = workoutBurn + watchActiveBurn
        val netIntake = (consumed - extraBurn).coerceAtLeast(0)
        val surplus = netIntake - target
        val kg = surplus / KCAL_PER_KG
        return EnergyBalance(
            consumed = consumed,
            targetKcal = target,
            workoutBurn = workoutBurn,
            watchActiveBurn = watchActiveBurn,
            netIntake = netIntake,
            remaining = (target - netIntake),
            projectedKgChange = kg,
            isSurplus = surplus > 0
        )
    }
}
