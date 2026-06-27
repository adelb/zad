package com.zad.app.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutRepository(private val dao: WorkoutDao) {

    fun routines(): Flow<List<Routine>> = dao.routines()
    fun exercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>> = dao.exercisesForRoutine(routineId)
    suspend fun routineById(id: Long) = dao.routineById(id)

    suspend fun createCustomRoutine(name: String, description: String,
                                    exercises: List<Triple<String, Int, Int>>): Long {
        val rid = dao.insertRoutine(Routine(nameAr = name, descriptionAr = description, isPreset = false))
        val items = exercises.mapIndexed { i, (exId, sets, reps) ->
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
        return rid
    }

    suspend fun deleteCustomRoutine(routineId: Long) {
        dao.clearRoutineExercises(routineId)
        dao.deleteUserRoutine(routineId)
    }

    suspend fun startSession(routineId: Long?, routineNameAr: String): Long {
        val now = System.currentTimeMillis()
        return dao.insertSession(
            WorkoutSession(
                routineId = routineId,
                routineNameAr = routineNameAr,
                startedAtMs = now,
                dayKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        )
    }

    suspend fun logSet(sessionId: Long, exerciseId: String, nameAr: String,
                       setNumber: Int, weightKg: Double, reps: Int): Long {
        return dao.insertSet(
            ExerciseSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                nameAr = nameAr,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps,
                recordedAtMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteSet(setId: Long) = dao.deleteSet(setId)

    fun setsForSession(sessionId: Long): Flow<List<ExerciseSet>> = dao.setsForSession(sessionId)
    fun recentSessions(): Flow<List<WorkoutSession>> = dao.recentSessions()
    fun topWeightPerSession(exerciseId: String) = dao.topWeightPerSession(exerciseId)
}
