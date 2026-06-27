package com.zad.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(e: WeightEntry): Long

    @Query("SELECT * FROM weight_entries ORDER BY recordedAtMs DESC LIMIT :limit")
    fun recent(limit: Int = 60): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY recordedAtMs DESC LIMIT 1")
    fun latest(): Flow<WeightEntry?>
}

@Dao
interface WaterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(e: WaterEntry): Long

    @Query("SELECT IFNULL(SUM(ml), 0) FROM water_entries WHERE dayKey = :dayKey")
    fun totalForDay(dayKey: String): Flow<Int>

    @Query("DELETE FROM water_entries WHERE dayKey = :dayKey")
    suspend fun clearForDay(dayKey: String)
}
