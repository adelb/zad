package com.zad.app.ml

/**
 * Per-exercise MET (metabolic equivalent). Values from the Compendium of
 * Physical Activities. Missing exercises fall back to a generic 4.5.
 */
object ExerciseMet {
    private val map: Map<String, Double> = mapOf(
        // chest — compound presses cluster ~5.0, fly ~4.5, bodyweight ~8.0
        "bench_press" to 5.0, "incline_press" to 5.0, "decline_press" to 5.0,
        "dumbbell_press" to 5.0, "incline_dumbbell_press" to 5.0, "decline_dumbbell_press" to 5.0,
        "dumbbell_fly" to 4.5, "cable_fly" to 4.5, "cable_crossover" to 4.5,
        "pec_deck" to 4.5, "machine_chest_press" to 4.5,
        "push_up" to 8.0, "diamond_push_up" to 8.0, "decline_push_up" to 8.5,
        "pullover" to 4.5, "chest_fly" to 4.5,

        // back — pulls / rows ~5.0, deadlifts ~6.0, weighted bodyweight ~8.0
        "deadlift" to 6.0, "sumo_deadlift" to 6.0, "trap_bar_dl" to 6.0,
        "rack_pull" to 5.5, "bent_row" to 5.0, "t_bar_row" to 5.0,
        "pendlay_row" to 5.5, "one_arm_row" to 5.0, "seated_row" to 4.5,
        "inverted_row" to 5.5, "lat_pulldown" to 4.5, "close_pulldown" to 4.5,
        "pull_up" to 8.0, "chin_up" to 8.0, "wide_pull_up" to 8.0,
        "face_pull" to 3.8, "shrug" to 3.5,
        "back_extension" to 4.0, "good_morning" to 4.5,

        // shoulders — presses 5.0, raises 3.5
        "shoulder_press" to 5.0, "military_press" to 5.5,
        "db_shoulder_press" to 5.0, "arnold_press" to 5.0, "push_press" to 6.0,
        "lateral_raise" to 3.5, "cable_lateral" to 3.5, "front_raise" to 3.5,
        "rear_delt_fly" to 3.5, "reverse_pec_deck" to 3.5, "upright_row" to 4.0,
        "handstand_push_up" to 9.0,

        // biceps / triceps / forearms — most isolation 3.5
        "bicep_curl" to 3.5, "hammer_curl" to 3.5, "preacher_curl" to 3.5,
        "concentration_curl" to 3.5, "spider_curl" to 3.5, "cable_curl" to 3.5,
        "ez_bar_curl" to 3.5, "incline_db_curl" to 3.5, "zottman_curl" to 3.5,
        "tricep_ext" to 3.5, "skullcrusher" to 3.8, "overhead_ext" to 3.8,
        "rope_pushdown" to 3.5, "cable_pushdown" to 3.5, "close_grip_bench" to 5.0,
        "dips" to 8.0, "bench_dips" to 5.0, "kickback" to 3.5,
        "wrist_curl" to 3.0, "reverse_wrist_curl" to 3.0,
        "farmer_walk" to 5.5, "dead_hang" to 3.5,

        // quads — squats heavy 6.0, isolation 4.0, plyometric 8.0
        "squat" to 6.0, "front_squat" to 6.0, "goblet_squat" to 5.5,
        "hack_squat" to 5.5, "leg_press" to 5.0, "leg_extension" to 4.0,
        "bulgarian_split" to 6.0, "step_up" to 5.5, "lunges" to 6.0,
        "walking_lunges" to 6.0, "jump_squat" to 8.0, "pistol_squat" to 6.0,

        // hamstrings
        "romanian_dl" to 5.5, "stiff_leg_dl" to 5.5, "leg_curl" to 4.0,
        "seated_leg_curl" to 4.0, "nordic_curl" to 6.0,
        "single_leg_dl" to 5.0, "glute_ham_raise" to 6.0,

        // glutes
        "hip_thrust" to 5.5, "glute_bridge" to 3.8, "glute_kickback" to 3.8,
        "cable_pull_through" to 4.5, "frog_pump" to 3.8, "hip_abduction" to 3.5,

        // calves
        "calf_raise" to 3.5, "standing_calf" to 3.5,
        "seated_calf" to 3.0, "donkey_calf" to 3.5,

        // core — most static 3.5-4.0, weighted dynamic up to 5.0
        "plank" to 3.5, "side_plank" to 3.5, "crunch" to 3.8,
        "bicycle_crunch" to 4.5, "cable_crunch" to 4.0,
        "leg_raise" to 3.8, "hanging_knee" to 5.0, "hanging_leg_raise" to 5.5,
        "russian_twist" to 4.0, "ab_wheel" to 5.0,
        "dead_bug" to 3.5, "bird_dog" to 3.5,
        "v_up" to 5.0, "toe_touch" to 4.0, "hollow_hold" to 4.0,

        // full body / functional
        "burpee" to 8.0, "clean" to 8.0, "snatch" to 9.0,
        "thruster" to 8.0, "kettlebell_swing" to 9.5,
        "turkish_getup" to 7.0, "box_jump" to 8.0, "broad_jump" to 7.5,
        "battle_rope" to 9.0, "sled_push" to 8.5,
        "bear_crawl" to 7.0, "jumping_jack" to 8.0, "mountain_climber" to 8.0,

        // cardio
        "treadmill_run" to 9.8, "outdoor_run" to 10.0,
        "cycle" to 7.5, "spin_bike" to 8.5, "rowing" to 7.0,
        "elliptical" to 5.0, "jump_rope" to 12.3,
        "stair_climber" to 9.0, "swimming" to 7.5
    )

    fun forExercise(id: String): Double = map[id] ?: 4.5
}

object BurnEstimator {

    /**
     * Estimate kcal burned for one set.
     *
     * Time under tension ≈ reps × 3 seconds; bundled with ~30s rest after.
     * kcal = MET × bodyweight × duration_hours.
     */
    fun forSet(exerciseId: String, reps: Int, bodyweightKg: Double): Int {
        val met = ExerciseMet.forExercise(exerciseId)
        val tutSec = reps.coerceAtLeast(1) * 3.0
        val totalSec = tutSec + 30.0
        val hours = totalSec / 3600.0
        val bw = if (bodyweightKg > 0) bodyweightKg else 75.0
        return (met * bw * hours).toInt().coerceAtLeast(1)
    }
}
