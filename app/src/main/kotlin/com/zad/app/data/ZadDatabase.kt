package com.zad.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MealEntry::class,
        Routine::class, RoutineExercise::class,
        WorkoutSession::class, ExerciseSet::class,
        WeightEntry::class, WaterEntry::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ZadDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun weightDao(): WeightDao
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile private var INSTANCE: ZadDatabase? = null
        fun get(context: Context): ZadDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ZadDatabase::class.java,
                "zad.db"
            )
            .fallbackToDestructiveMigration()
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed presets after Room finishes building.
                    CoroutineScope(Dispatchers.IO).launch {
                        INSTANCE?.workoutDao()?.let { dao ->
                            seedPresetRoutines(dao)
                        }
                    }
                }
            })
            .build().also { INSTANCE = it }
        }

        private suspend fun seedPresetRoutines(dao: WorkoutDao) {
            if (dao.routineCount() > 0) return
            PresetRoutines.ALL.forEach { preset ->
                val rid = dao.insertRoutine(
                    Routine(nameAr = preset.nameAr, descriptionAr = preset.descriptionAr, isPreset = true)
                )
                val items = preset.exerciseIds.mapIndexed { i, (exId, sets, reps) ->
                    val ex = ExerciseCatalog.byId(exId)
                    RoutineExercise(
                        routineId = rid,
                        orderIndex = i,
                        exerciseId = exId,
                        nameAr = ex?.nameAr ?: exId,
                        targetSets = sets,
                        targetReps = reps
                    )
                }
                dao.insertRoutineExercises(items)
            }
        }
    }
}
