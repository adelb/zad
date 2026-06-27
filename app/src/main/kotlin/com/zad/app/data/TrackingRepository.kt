package com.zad.app.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrackingRepository(
    private val weightDao: WeightDao,
    private val waterDao: WaterDao
) {
    private fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    // ── Weight ──
    suspend fun logWeight(kg: Double): Long {
        val now = System.currentTimeMillis()
        return weightDao.insert(WeightEntry(recordedAtMs = now, dayKey = today(), kg = kg))
    }

    fun recentWeights(): Flow<List<WeightEntry>> = weightDao.recent()
    fun latestWeight(): Flow<WeightEntry?> = weightDao.latest()

    // ── Water ──
    suspend fun addWater(ml: Int): Long {
        val now = System.currentTimeMillis()
        return waterDao.insert(WaterEntry(recordedAtMs = now, dayKey = today(), ml = ml))
    }

    fun todayWaterMl(): Flow<Int> = waterDao.totalForDay(today())

    suspend fun clearTodayWater() = waterDao.clearForDay(today())
}
