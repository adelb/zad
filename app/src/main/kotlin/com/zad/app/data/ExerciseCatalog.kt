package com.zad.app.data

data class Exercise(
    val id: String,
    val nameAr: String,
    val muscleGroup: MuscleGroup
)

enum class MuscleGroup(val arabicLabel: String) {
    CHEST("صدر"),
    BACK("ظهر"),
    SHOULDERS("أكتاف"),
    ARMS("ذراعين"),
    LEGS("أرجل"),
    CORE("بطن"),
    FULL_BODY("جسم كامل"),
    CARDIO("كارديو")
}

object ExerciseCatalog {
    val ALL: List<Exercise> = listOf(
        // chest
        Exercise("bench_press",      "بنش برس",            MuscleGroup.CHEST),
        Exercise("incline_press",    "بنش مائل",           MuscleGroup.CHEST),
        Exercise("dumbbell_press",   "بنش دامبل",          MuscleGroup.CHEST),
        Exercise("chest_fly",        "فلاي للصدر",         MuscleGroup.CHEST),
        Exercise("push_up",          "بوش أب",             MuscleGroup.CHEST),
        // back
        Exercise("deadlift",         "ديدليفت",            MuscleGroup.BACK),
        Exercise("bent_row",         "بنت أوفر رو",        MuscleGroup.BACK),
        Exercise("lat_pulldown",     "لاتس بول داون",      MuscleGroup.BACK),
        Exercise("pull_up",          "بول أب",             MuscleGroup.BACK),
        Exercise("seated_row",       "سيتد رو",            MuscleGroup.BACK),
        // shoulders
        Exercise("shoulder_press",   "شولدر برس",          MuscleGroup.SHOULDERS),
        Exercise("lateral_raise",    "لاتيرال رايز",       MuscleGroup.SHOULDERS),
        Exercise("front_raise",      "فرونت رايز",         MuscleGroup.SHOULDERS),
        Exercise("rear_delt_fly",    "ريير دلت فلاي",      MuscleGroup.SHOULDERS),
        // arms
        Exercise("bicep_curl",       "بايسبس كيرل",        MuscleGroup.ARMS),
        Exercise("hammer_curl",      "هامر كيرل",          MuscleGroup.ARMS),
        Exercise("tricep_ext",       "تراي إكستنشن",       MuscleGroup.ARMS),
        Exercise("dips",             "ديب",                MuscleGroup.ARMS),
        Exercise("preacher_curl",    "بريتشر كيرل",        MuscleGroup.ARMS),
        // legs
        Exercise("squat",            "سكوات",              MuscleGroup.LEGS),
        Exercise("leg_press",        "ليج برس",            MuscleGroup.LEGS),
        Exercise("leg_curl",         "ليج كيرل",           MuscleGroup.LEGS),
        Exercise("leg_extension",    "ليج إكستنشن",        MuscleGroup.LEGS),
        Exercise("lunges",           "لانجز",              MuscleGroup.LEGS),
        Exercise("romanian_dl",      "رومانيان ديدليفت",   MuscleGroup.LEGS),
        Exercise("calf_raise",       "كافس",               MuscleGroup.LEGS),
        // core
        Exercise("plank",            "بلانك",              MuscleGroup.CORE),
        Exercise("crunch",           "كرنش",               MuscleGroup.CORE),
        Exercise("leg_raise",        "ليج رايز",           MuscleGroup.CORE),
        Exercise("russian_twist",    "روسيان تويست",       MuscleGroup.CORE),
        Exercise("hanging_knee",     "هانجينج ني رايز",    MuscleGroup.CORE),
        // full / cardio
        Exercise("burpee",           "بيربي",              MuscleGroup.FULL_BODY),
        Exercise("mountain_climber", "ماونتن كلايمبر",     MuscleGroup.FULL_BODY),
        Exercise("kettlebell_swing", "كيتل بيل سوينج",     MuscleGroup.FULL_BODY),
        Exercise("treadmill_run",    "جري",                MuscleGroup.CARDIO),
        Exercise("cycle",            "دراجة",              MuscleGroup.CARDIO),
        Exercise("rowing",           "رويينج",             MuscleGroup.CARDIO)
    )

    private val byId = ALL.associateBy { it.id }
    fun byId(id: String): Exercise? = byId[id]
    fun byMuscle(g: MuscleGroup): List<Exercise> = ALL.filter { it.muscleGroup == g }
}
