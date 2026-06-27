package com.zad.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class SessionWithSets(
    val session: WorkoutSession,
    val sets: List<ExerciseSet>
)

@Dao
interface WorkoutDao {

    // routines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(r: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(items: List<RoutineExercise>): List<Long>

    @Query("SELECT * FROM routines ORDER BY isPreset DESC, id ASC")
    fun routines(): Flow<List<Routine>>

    @Query("SELECT COUNT(*) FROM routines")
    suspend fun routineCount(): Int

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun exercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    suspend fun routineExercisesNow(routineId: Long): List<RoutineExercise>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun routineById(id: Long): Routine?

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun clearRoutineExercises(routineId: Long)

    @Query("DELETE FROM routines WHERE id = :routineId AND isPreset = 0")
    suspend fun deleteUserRoutine(routineId: Long)

    // sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(s: WorkoutSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(s: ExerciseSet): Long

    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun deleteSet(id: Long)

    @Query("SELECT * FROM workout_sessions ORDER BY startedAtMs DESC LIMIT :limit")
    fun recentSessions(limit: Int = 30): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    fun setsForSession(sessionId: Long): Flow<List<ExerciseSet>>

    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY startedAtMs DESC LIMIT :limit")
    suspend fun recentWithSets(limit: Int = 30): List<WorkoutSession>

    /** Top weight for an exercise across each session — used by the lift chart. */
    @Query("""
        SELECT s.startedAtMs AS startedAtMs, MAX(es.weightKg) AS topWeight
        FROM workout_sessions s
        INNER JOIN exercise_sets es ON es.sessionId = s.id
        WHERE es.exerciseId = :exerciseId
        GROUP BY s.id
        ORDER BY s.startedAtMs ASC
        LIMIT 30
    """)
    fun topWeightPerSession(exerciseId: String): Flow<List<TopWeightPoint>>

    /** Calories burned for a session — sum across all sets. */
    @Query("SELECT IFNULL(SUM(caloriesEstimate),0) FROM exercise_sets WHERE sessionId = :sessionId")
    fun caloriesForSession(sessionId: Long): Flow<Int>

    /** Total calories burned across all sessions on this day. */
    @Query("""
        SELECT IFNULL(SUM(es.caloriesEstimate),0)
        FROM workout_sessions s
        INNER JOIN exercise_sets es ON es.sessionId = s.id
        WHERE s.dayKey = :dayKey
    """)
    fun caloriesForDay(dayKey: String): Flow<Int>
}

data class TopWeightPoint(val startedAtMs: Long, val topWeight: Double)
