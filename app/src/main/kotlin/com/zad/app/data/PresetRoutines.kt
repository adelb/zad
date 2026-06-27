package com.zad.app.data

data class PresetRoutine(
    val nameAr: String,
    val descriptionAr: String,
    val exerciseIds: List<Triple<String, Int, Int>>  // (exerciseId, sets, reps)
)

object PresetRoutines {
    val ALL: List<PresetRoutine> = listOf(
        PresetRoutine(
            nameAr = "روتين المبتدئ — الجسم الكامل",
            descriptionAr = "ثلاث جلسات أسبوعيًا، حركات أساسية",
            exerciseIds = listOf(
                Triple("squat",         3, 10),
                Triple("bench_press",   3, 10),
                Triple("bent_row",      3, 10),
                Triple("shoulder_press",3, 10),
                Triple("plank",         3, 30)  // 30 seconds
            )
        ),
        PresetRoutine(
            nameAr = "يوم الدفع — صدر وأكتاف وترايسبس",
            descriptionAr = "Push day كلاسيكي",
            exerciseIds = listOf(
                Triple("bench_press",     4,  8),
                Triple("incline_press",   3, 10),
                Triple("shoulder_press",  3, 10),
                Triple("lateral_raise",   3, 12),
                Triple("tricep_ext",      3, 12),
                Triple("dips",            3, 10)
            )
        ),
        PresetRoutine(
            nameAr = "يوم السحب — ظهر وبايسبس",
            descriptionAr = "Pull day كامل",
            exerciseIds = listOf(
                Triple("deadlift",     4,  6),
                Triple("pull_up",      3,  8),
                Triple("bent_row",     3, 10),
                Triple("lat_pulldown", 3, 10),
                Triple("bicep_curl",   3, 12),
                Triple("hammer_curl",  3, 12)
            )
        ),
        PresetRoutine(
            nameAr = "يوم الأرجل",
            descriptionAr = "Leg day كامل",
            exerciseIds = listOf(
                Triple("squat",         4,  8),
                Triple("romanian_dl",   3, 10),
                Triple("leg_press",     3, 10),
                Triple("leg_curl",      3, 12),
                Triple("leg_extension", 3, 12),
                Triple("calf_raise",    3, 15)
            )
        ),
        PresetRoutine(
            nameAr = "روتين البطن السريع",
            descriptionAr = "١٥ دقيقة، أربع حركات",
            exerciseIds = listOf(
                Triple("plank",          3, 45),
                Triple("crunch",         3, 20),
                Triple("leg_raise",      3, 15),
                Triple("russian_twist",  3, 30),
                Triple("hanging_knee",   3, 12)
            )
        ),
        PresetRoutine(
            nameAr = "الجزء العلوي — كامل",
            descriptionAr = "صدر، ظهر، أكتاف، ذراعين في جلسة واحدة",
            exerciseIds = listOf(
                Triple("bench_press",   4, 8),
                Triple("bent_row",      4, 8),
                Triple("shoulder_press",3, 10),
                Triple("lat_pulldown",  3, 10),
                Triple("bicep_curl",    3, 12),
                Triple("tricep_ext",    3, 12)
            )
        ),
        PresetRoutine(
            nameAr = "الجزء السفلي — كامل",
            descriptionAr = "أرجل وأرداف وعجلات",
            exerciseIds = listOf(
                Triple("squat",        4,  8),
                Triple("romanian_dl",  4,  8),
                Triple("leg_press",    3, 10),
                Triple("lunges",       3, 12),
                Triple("leg_curl",     3, 12),
                Triple("calf_raise",   4, 15)
            )
        ),
        PresetRoutine(
            nameAr = "روتين الذراعين",
            descriptionAr = "بايسبس وترايسبس متخصص",
            exerciseIds = listOf(
                Triple("bicep_curl",     4, 10),
                Triple("hammer_curl",    3, 12),
                Triple("preacher_curl",  3, 10),
                Triple("tricep_ext",     4, 10),
                Triple("dips",           3, 10)
            )
        ),
        PresetRoutine(
            nameAr = "روتين الأكتاف",
            descriptionAr = "أكتاف ثلاثية الرؤوس",
            exerciseIds = listOf(
                Triple("shoulder_press", 4, 8),
                Triple("lateral_raise",  4, 12),
                Triple("front_raise",    3, 12),
                Triple("rear_delt_fly",  3, 12)
            )
        ),
        PresetRoutine(
            nameAr = "روتين الظهر العريض",
            descriptionAr = "بناء ظهر V-shape",
            exerciseIds = listOf(
                Triple("deadlift",      4, 6),
                Triple("pull_up",       4, 8),
                Triple("lat_pulldown",  4, 10),
                Triple("seated_row",    3, 10),
                Triple("bent_row",      3, 10)
            )
        ),
        PresetRoutine(
            nameAr = "وزن الجسم فقط — بدون معدّات",
            descriptionAr = "في البيت، بدون أي أوزان",
            exerciseIds = listOf(
                Triple("push_up",          4, 15),
                Triple("squat",            4, 20),
                Triple("lunges",           3, 12),
                Triple("plank",            3, 45),
                Triple("burpee",           3, 10),
                Triple("mountain_climber", 3, 30)
            )
        ),
        PresetRoutine(
            nameAr = "هايت — كارديو حارق",
            descriptionAr = "٢٠ دقيقة، نبض عالٍ",
            exerciseIds = listOf(
                Triple("burpee",           4, 12),
                Triple("mountain_climber", 4, 40),
                Triple("kettlebell_swing", 4, 20),
                Triple("rowing",           4, 60),
                Triple("squat",            4, 20)
            )
        ),
        PresetRoutine(
            nameAr = "كارديو طويل — تحمّل",
            descriptionAr = "٤٠–٦٠ دقيقة منخفض الشدة",
            exerciseIds = listOf(
                Triple("treadmill_run", 1, 30),  // 30 minutes
                Triple("cycle",         1, 20),
                Triple("rowing",        1, 10)
            )
        ),
        PresetRoutine(
            nameAr = "5×5 الكلاسيكي",
            descriptionAr = "قوة ثقيلة، خمس مجموعات × خمس عدّات",
            exerciseIds = listOf(
                Triple("squat",         5, 5),
                Triple("bench_press",   5, 5),
                Triple("bent_row",      5, 5),
                Triple("deadlift",      1, 5),
                Triple("shoulder_press",5, 5)
            )
        ),
        PresetRoutine(
            nameAr = "روتين سريع — ٢٠ دقيقة",
            descriptionAr = "للأيام المزدحمة",
            exerciseIds = listOf(
                Triple("squat",       3, 10),
                Triple("push_up",     3, 12),
                Triple("bent_row",    3, 10),
                Triple("plank",       3, 30),
                Triple("burpee",      3, 8)
            )
        )
    )
}
