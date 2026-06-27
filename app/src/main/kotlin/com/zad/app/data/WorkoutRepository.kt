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

    /**
     * Replace the exercise list for an existing routine. Used to edit a routine —
     * works for both preset and custom routines while keeping the routine id
     * stable (so any active session still references the same plan).
     */
    suspend fun updateRoutineExercises(
        routineId: Long,
        exercises: List<Triple<String, Int, Int>>
    ) {
        dao.clearRoutineExercises(routineId)
        val items = exercises.mapIndexed { i, (exId, sets, reps) ->
            val ex = ExerciseCatalog.byId(exId)
            RoutineExercise(
                routineId = routineId,
                orderIndex = i,
                exerciseId = exId,
                nameAr = ex?.nameAr ?: exId,
                targetSets = sets,
                targetReps = reps
            )
        }
        dao.insertRoutineExercises(items)
    }

    /**
     * Append an ad-hoc exercise to an existing routine's plan — used from the
     * Session screen so the user can add an exercise that wasn't planned.
     */
    suspend fun appendExerciseToRoutine(
        routineId: Long,
        exerciseId: String,
        targetSets: Int = 3,
        targetReps: Int = 10
    ) {
        val current = dao.routineExercisesNow(routineId)
        val ex = ExerciseCatalog.byId(exerciseId)
        dao.insertRoutineExercises(
            listOf(
                RoutineExercise(
                    routineId = routineId,
                    orderIndex = current.size,
                    exerciseId = exerciseId,
                    nameAr = ex?.nameAr ?: exerciseId,
                    targetSets = targetSets,
                    targetReps = targetReps
                )
            )
        )
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
                       setNumber: Int, weightKg: Double, reps: Int,
                       bodyweightKg: Double): Long {
        val kcal = com.zad.app.ml.BurnEstimator.forSet(exerciseId, reps, bodyweightKg)
        return dao.insertSet(
            ExerciseSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                nameAr = nameAr,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps,
                recordedAtMs = System.currentTimeMillis(),
                caloriesEstimate = kcal
            )
        )
    }

    suspend fun deleteSet(setId: Long) = dao.deleteSet(setId)

    fun setsForSession(sessionId: Long): Flow<List<ExerciseSet>> = dao.setsForSession(sessionId)
    fun recentSessions(): Flow<List<WorkoutSession>> = dao.recentSessions()
    fun topWeightPerSession(exerciseId: String) = dao.topWeightPerSession(exerciseId)
    fun caloriesForSession(sessionId: Long): Flow<Int> = dao.caloriesForSession(sessionId)
    fun caloriesBurnedToday(): Flow<Int> = dao.caloriesForDay(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
}
