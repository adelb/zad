package com.zad.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameAr: String,
    val descriptionAr: String,
    val isPreset: Boolean
)

@Entity(tableName = "routine_exercises")
data class RoutineExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val orderIndex: Int,
    val exerciseId: String,
    val nameAr: String,
    val targetSets: Int,
    val targetReps: Int
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long?,
    val routineNameAr: String,
    val startedAtMs: Long,
    val dayKey: String
)

@Entity(tableName = "exercise_sets")
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val nameAr: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val recordedAtMs: Long
)
