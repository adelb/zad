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
    BICEPS("بايسبس"),
    TRICEPS("ترايسبس"),
    FOREARMS("سواعد"),
    QUADS("أمامية الفخذ"),
    HAMSTRINGS("خلفية الفخذ"),
    GLUTES("أرداف"),
    CALVES("سمانة"),
    CORE("بطن"),
    FULL_BODY("جسم كامل"),
    CARDIO("كارديو")
}

object ExerciseCatalog {
    val ALL: List<Exercise> = listOf(
        // ─── CHEST ───
        Exercise("bench_press",            "بنش برس بالبار",        MuscleGroup.CHEST),
        Exercise("incline_press",          "بنش مائل بالبار",       MuscleGroup.CHEST),
        Exercise("decline_press",          "بنش هابط بالبار",       MuscleGroup.CHEST),
        Exercise("dumbbell_press",         "بنش دامبل مستوي",       MuscleGroup.CHEST),
        Exercise("incline_dumbbell_press", "بنش دامبل مائل",        MuscleGroup.CHEST),
        Exercise("decline_dumbbell_press", "بنش دامبل هابط",        MuscleGroup.CHEST),
        Exercise("dumbbell_fly",           "فلاي دامبل",            MuscleGroup.CHEST),
        Exercise("cable_fly",              "فلاي كيبل",             MuscleGroup.CHEST),
        Exercise("cable_crossover",        "كيبل كروس أوفر",        MuscleGroup.CHEST),
        Exercise("pec_deck",               "بيك ديك",               MuscleGroup.CHEST),
        Exercise("machine_chest_press",    "جهاز بنش",              MuscleGroup.CHEST),
        Exercise("push_up",                "بوش أب",                MuscleGroup.CHEST),
        Exercise("diamond_push_up",        "بوش أب ماسة",           MuscleGroup.CHEST),
        Exercise("decline_push_up",        "بوش أب هابط",           MuscleGroup.CHEST),
        Exercise("pullover",               "بول أوفر",              MuscleGroup.CHEST),
        Exercise("chest_fly",              "فلاي للصدر",            MuscleGroup.CHEST),

        // ─── BACK ───
        Exercise("deadlift",          "ديدليفت",              MuscleGroup.BACK),
        Exercise("sumo_deadlift",     "ديدليفت سومو",          MuscleGroup.BACK),
        Exercise("trap_bar_dl",       "تراب بار ديدليفت",      MuscleGroup.BACK),
        Exercise("rack_pull",         "راك بول",              MuscleGroup.BACK),
        Exercise("bent_row",          "بنت أوفر رو",          MuscleGroup.BACK),
        Exercise("t_bar_row",         "تي بار رو",            MuscleGroup.BACK),
        Exercise("pendlay_row",       "بندلاي رو",            MuscleGroup.BACK),
        Exercise("one_arm_row",       "ون آرم دامبل رو",       MuscleGroup.BACK),
        Exercise("seated_row",        "سيتد رو",              MuscleGroup.BACK),
        Exercise("inverted_row",      "إنفرتد رو",            MuscleGroup.BACK),
        Exercise("lat_pulldown",      "لاتس بول داون",         MuscleGroup.BACK),
        Exercise("close_pulldown",    "بول داون قبضة ضيقة",    MuscleGroup.BACK),
        Exercise("pull_up",           "بول أب",               MuscleGroup.BACK),
        Exercise("chin_up",           "تشين أب",              MuscleGroup.BACK),
        Exercise("wide_pull_up",      "بول أب قبضة واسعة",    MuscleGroup.BACK),
        Exercise("face_pull",         "فيس بول",              MuscleGroup.BACK),
        Exercise("shrug",             "شراج",                MuscleGroup.BACK),
        Exercise("back_extension",    "باك إكستنشن",          MuscleGroup.BACK),
        Exercise("good_morning",      "جود مورنينج",          MuscleGroup.BACK),

        // ─── SHOULDERS ───
        Exercise("shoulder_press",       "شولدر برس بالبار",     MuscleGroup.SHOULDERS),
        Exercise("military_press",       "ميليتاري برس",         MuscleGroup.SHOULDERS),
        Exercise("db_shoulder_press",    "دامبل شولدر برس",      MuscleGroup.SHOULDERS),
        Exercise("arnold_press",         "آرنولد برس",          MuscleGroup.SHOULDERS),
        Exercise("push_press",           "بوش برس",             MuscleGroup.SHOULDERS),
        Exercise("lateral_raise",        "لاتيرال رايز",         MuscleGroup.SHOULDERS),
        Exercise("cable_lateral",        "كيبل لاتيرال",         MuscleGroup.SHOULDERS),
        Exercise("front_raise",          "فرونت رايز",          MuscleGroup.SHOULDERS),
        Exercise("rear_delt_fly",        "ريير دلت فلاي",        MuscleGroup.SHOULDERS),
        Exercise("reverse_pec_deck",     "ريفيرس بيك ديك",       MuscleGroup.SHOULDERS),
        Exercise("upright_row",          "أبرايت رو",           MuscleGroup.SHOULDERS),
        Exercise("handstand_push_up",    "هاندستاند بوش أب",     MuscleGroup.SHOULDERS),

        // ─── BICEPS ───
        Exercise("bicep_curl",         "بايسبس كيرل",         MuscleGroup.BICEPS),
        Exercise("hammer_curl",        "هامر كيرل",           MuscleGroup.BICEPS),
        Exercise("preacher_curl",      "بريتشر كيرل",         MuscleGroup.BICEPS),
        Exercise("concentration_curl", "كونسنتريشن كيرل",      MuscleGroup.BICEPS),
        Exercise("spider_curl",        "سبايدر كيرل",         MuscleGroup.BICEPS),
        Exercise("cable_curl",         "كيبل كيرل",           MuscleGroup.BICEPS),
        Exercise("ez_bar_curl",        "إي زد بار كيرل",      MuscleGroup.BICEPS),
        Exercise("incline_db_curl",    "دامبل كيرل مائل",     MuscleGroup.BICEPS),
        Exercise("zottman_curl",       "زوتمان كيرل",         MuscleGroup.BICEPS),

        // ─── TRICEPS ───
        Exercise("tricep_ext",       "تراي إكستنشن",       MuscleGroup.TRICEPS),
        Exercise("skullcrusher",     "سكال كرشر",          MuscleGroup.TRICEPS),
        Exercise("overhead_ext",     "أوفرهيد ترايسبس",    MuscleGroup.TRICEPS),
        Exercise("rope_pushdown",    "روب بوش داون",       MuscleGroup.TRICEPS),
        Exercise("cable_pushdown",   "كيبل بوش داون",      MuscleGroup.TRICEPS),
        Exercise("close_grip_bench", "كلوز جريب بنش",      MuscleGroup.TRICEPS),
        Exercise("dips",             "ديب",                MuscleGroup.TRICEPS),
        Exercise("bench_dips",       "بنش ديب",            MuscleGroup.TRICEPS),
        Exercise("kickback",         "كيك باك",            MuscleGroup.TRICEPS),

        // ─── FOREARMS ───
        Exercise("wrist_curl",         "كيرل المعصم",         MuscleGroup.FOREARMS),
        Exercise("reverse_wrist_curl", "كيرل عكسي للمعصم",     MuscleGroup.FOREARMS),
        Exercise("farmer_walk",        "فارمر ووك",           MuscleGroup.FOREARMS),
        Exercise("dead_hang",          "ديد هانج",            MuscleGroup.FOREARMS),

        // ─── QUADS ───
        Exercise("squat",                "سكوات",                  MuscleGroup.QUADS),
        Exercise("front_squat",          "فرونت سكوات",            MuscleGroup.QUADS),
        Exercise("goblet_squat",         "جوبليت سكوات",           MuscleGroup.QUADS),
        Exercise("hack_squat",           "هاك سكوات",              MuscleGroup.QUADS),
        Exercise("leg_press",            "ليج برس",                MuscleGroup.QUADS),
        Exercise("leg_extension",        "ليج إكستنشن",            MuscleGroup.QUADS),
        Exercise("bulgarian_split",      "بلغاريان سبليت سكوات",    MuscleGroup.QUADS),
        Exercise("step_up",              "ستيب أب",                MuscleGroup.QUADS),
        Exercise("lunges",               "لانجز",                  MuscleGroup.QUADS),
        Exercise("walking_lunges",       "لانجز متحركة",           MuscleGroup.QUADS),
        Exercise("jump_squat",           "سكوات بقفز",             MuscleGroup.QUADS),
        Exercise("pistol_squat",         "بيستول سكوات",           MuscleGroup.QUADS),

        // ─── HAMSTRINGS ───
        Exercise("romanian_dl",     "رومانيان ديدليفت",   MuscleGroup.HAMSTRINGS),
        Exercise("stiff_leg_dl",    "ستيف ليج ديدليفت",   MuscleGroup.HAMSTRINGS),
        Exercise("leg_curl",        "ليج كيرل",          MuscleGroup.HAMSTRINGS),
        Exercise("seated_leg_curl", "ليج كيرل جالس",     MuscleGroup.HAMSTRINGS),
        Exercise("nordic_curl",     "نورديك كيرل",       MuscleGroup.HAMSTRINGS),
        Exercise("single_leg_dl",   "سينجل ليج ديدليفت",  MuscleGroup.HAMSTRINGS),
        Exercise("glute_ham_raise", "جلوت هام رايز",     MuscleGroup.HAMSTRINGS),

        // ─── GLUTES ───
        Exercise("hip_thrust",        "هيب ثرست",           MuscleGroup.GLUTES),
        Exercise("glute_bridge",      "جلوت بريدج",         MuscleGroup.GLUTES),
        Exercise("glute_kickback",    "جلوت كيك باك",       MuscleGroup.GLUTES),
        Exercise("cable_pull_through","كيبل بول ثرو",       MuscleGroup.GLUTES),
        Exercise("frog_pump",         "فروج بامب",          MuscleGroup.GLUTES),
        Exercise("hip_abduction",     "هيب أبدكشن",        MuscleGroup.GLUTES),

        // ─── CALVES ───
        Exercise("calf_raise",       "كافس",            MuscleGroup.CALVES),
        Exercise("standing_calf",    "كافس واقف",       MuscleGroup.CALVES),
        Exercise("seated_calf",      "كافس جالس",       MuscleGroup.CALVES),
        Exercise("donkey_calf",      "دونكي كافس",      MuscleGroup.CALVES),

        // ─── CORE ───
        Exercise("plank",              "بلانك",                MuscleGroup.CORE),
        Exercise("side_plank",         "سايد بلانك",           MuscleGroup.CORE),
        Exercise("crunch",             "كرنش",                MuscleGroup.CORE),
        Exercise("bicycle_crunch",     "بايسكل كرنش",          MuscleGroup.CORE),
        Exercise("cable_crunch",       "كيبل كرنش",            MuscleGroup.CORE),
        Exercise("leg_raise",          "ليج رايز",             MuscleGroup.CORE),
        Exercise("hanging_knee",       "هانجينج ني رايز",      MuscleGroup.CORE),
        Exercise("hanging_leg_raise",  "هانجينج ليج رايز",     MuscleGroup.CORE),
        Exercise("russian_twist",      "روسيان تويست",         MuscleGroup.CORE),
        Exercise("ab_wheel",           "آب ويل",              MuscleGroup.CORE),
        Exercise("dead_bug",           "ديد باج",             MuscleGroup.CORE),
        Exercise("bird_dog",           "بيرد دوج",             MuscleGroup.CORE),
        Exercise("v_up",               "في أب",               MuscleGroup.CORE),
        Exercise("toe_touch",          "تو تاتش",              MuscleGroup.CORE),
        Exercise("hollow_hold",        "هولو هولد",            MuscleGroup.CORE),

        // ─── FULL BODY / FUNCTIONAL ───
        Exercise("burpee",           "بيربي",                MuscleGroup.FULL_BODY),
        Exercise("clean",            "كلين",                 MuscleGroup.FULL_BODY),
        Exercise("snatch",           "سناتش",                MuscleGroup.FULL_BODY),
        Exercise("thruster",         "ثرستر",                MuscleGroup.FULL_BODY),
        Exercise("kettlebell_swing", "كيتل بيل سوينج",        MuscleGroup.FULL_BODY),
        Exercise("turkish_getup",    "تركيش جت أب",          MuscleGroup.FULL_BODY),
        Exercise("box_jump",         "بوكس جامب",            MuscleGroup.FULL_BODY),
        Exercise("broad_jump",       "برود جامب",            MuscleGroup.FULL_BODY),
        Exercise("battle_rope",      "بات روب",              MuscleGroup.FULL_BODY),
        Exercise("sled_push",        "سليد بوش",             MuscleGroup.FULL_BODY),
        Exercise("bear_crawl",       "بير كرول",             MuscleGroup.FULL_BODY),
        Exercise("jumping_jack",     "جامبينج جاك",          MuscleGroup.FULL_BODY),
        Exercise("mountain_climber", "ماونتن كلايمبر",       MuscleGroup.FULL_BODY),

        // ─── CARDIO ───
        Exercise("treadmill_run", "جري على السير", MuscleGroup.CARDIO),
        Exercise("outdoor_run",   "جري خارجي",     MuscleGroup.CARDIO),
        Exercise("cycle",         "دراجة",        MuscleGroup.CARDIO),
        Exercise("spin_bike",     "دراجة سبين",   MuscleGroup.CARDIO),
        Exercise("rowing",        "رويينج",       MuscleGroup.CARDIO),
        Exercise("elliptical",    "إليبتيكال",    MuscleGroup.CARDIO),
        Exercise("jump_rope",     "نط الحبل",     MuscleGroup.CARDIO),
        Exercise("stair_climber", "سلالم",        MuscleGroup.CARDIO),
        Exercise("swimming",      "سباحة",        MuscleGroup.CARDIO)
    )

    private val byId = ALL.associateBy { it.id }
    fun byId(id: String): Exercise? = byId[id]
    fun byMuscle(g: MuscleGroup): List<Exercise> = ALL.filter { it.muscleGroup == g }
}
