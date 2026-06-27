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
        )
    )
}
