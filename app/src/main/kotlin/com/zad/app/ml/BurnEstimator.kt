package com.zad.app.ml

/**
 * Per-exercise MET (metabolic equivalent) — from the Compendium of Physical
 * Activities. We use a single representative MET per movement; intensity
 * scales naturally because heavier weight + more reps = longer time under load.
 */
object ExerciseMet {
    private val map: Map<String, Double> = mapOf(
        // chest
        "bench_press"      to 5.0,
        "incline_press"    to 5.0,
        "dumbbell_press"   to 5.0,
        "chest_fly"        to 4.5,
        "push_up"          to 8.0,
        // back
        "deadlift"         to 6.0,
        "bent_row"         to 5.0,
        "lat_pulldown"     to 4.5,
        "pull_up"          to 8.0,
        "seated_row"       to 4.5,
        // shoulders
        "shoulder_press"   to 5.0,
        "lateral_raise"    to 3.5,
        "front_raise"      to 3.5,
        "rear_delt_fly"    to 3.5,
        // arms
        "bicep_curl"       to 3.5,
        "hammer_curl"      to 3.5,
        "tricep_ext"       to 3.5,
        "dips"             to 8.0,
        "preacher_curl"    to 3.5,
        // legs
        "squat"            to 6.0,
        "leg_press"        to 5.0,
        "leg_curl"         to 4.0,
        "leg_extension"    to 4.0,
        "lunges"           to 6.0,
        "romanian_dl"      to 5.5,
        "calf_raise"       to 3.5,
        // core
        "plank"            to 3.5,
        "crunch"           to 3.8,
        "leg_raise"        to 3.8,
        "russian_twist"    to 4.0,
        "hanging_knee"     to 5.0,
        // full / cardio
        "burpee"           to 8.0,
        "mountain_climber" to 8.0,
        "kettlebell_swing" to 9.5,
        "treadmill_run"    to 9.8,
        "cycle"            to 7.5,
        "rowing"           to 7.0
    )

    fun forExercise(id: String): Double = map[id] ?: 4.5
}

object BurnEstimator {

    /**
     * Estimate kcal burned for one set.
     *
     * Time under tension ≈ reps × 3 seconds (one full rep ≈ 3s eccentric+concentric).
     * Rest assumed bundled with the set — total minute-per-set window ≈ tut + ~30s breath.
     * kcal = MET × bodyweight × duration_hours
     *
     * If the user hasn't entered bodyweight yet (no profile), we fall back
     * to 75 kg — enough to give a sane ballpark without a crash.
     */
    fun forSet(exerciseId: String, reps: Int, bodyweightKg: Double): Int {
        val met = ExerciseMet.forExercise(exerciseId)
        val tutSec = (reps.coerceAtLeast(1) * 3.0)
        val totalSec = tutSec + 30.0
        val hours = totalSec / 3600.0
        val bw = if (bodyweightKg > 0) bodyweightKg else 75.0
        return (met * bw * hours).toInt().coerceAtLeast(1)
    }
}
