package com.zad.app.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.ZoneId

enum class HcStatus { AVAILABLE, NEEDS_UPDATE, NOT_INSTALLED }

data class HcReading(
    val activeKcal: Int = 0,
    val totalKcal: Int = 0,
    val steps: Int = 0,
    val distanceMeters: Int = 0,
    val exerciseMinutes: Int = 0,
    val granted: Boolean = false,
    val status: HcStatus = HcStatus.NOT_INSTALLED,
    val lastUpdatedMs: Long = 0L
)

class HealthConnectBridge(private val context: Context) {

    val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    fun status(): HcStatus = when (HealthConnectClient.getSdkStatus(context)) {
        HealthConnectClient.SDK_AVAILABLE -> HcStatus.AVAILABLE
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HcStatus.NEEDS_UPDATE
        else -> HcStatus.NOT_INSTALLED
    }

    private fun client(): HealthConnectClient? =
        if (status() == HcStatus.AVAILABLE) HealthConnectClient.getOrCreate(context) else null

    fun permissionContract() = PermissionController.createRequestPermissionResultContract()

    suspend fun hasPermissions(): Boolean {
        val c = client() ?: return false
        return c.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    /** Today's totals — pulled in parallel-ish, never throws. */
    suspend fun readToday(): HcReading {
        val st = status()
        if (st != HcStatus.AVAILABLE) return HcReading(status = st)
        val c = client() ?: return HcReading(status = st)
        if (!c.permissionController.getGrantedPermissions().containsAll(permissions)) {
            return HcReading(status = st, granted = false)
        }

        val zone = ZoneId.systemDefault()
        val start = LocalDate.now().atStartOfDay(zone).toInstant()
        val end = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant()
        val range = TimeRangeFilter.between(start, end)

        val activeKcal = runCatching {
            c.readRecords(ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, range))
                .records.sumOf { it.energy.inKilocalories }
        }.getOrDefault(0.0)

        val totalKcal = runCatching {
            c.readRecords(ReadRecordsRequest(TotalCaloriesBurnedRecord::class, range))
                .records.sumOf { it.energy.inKilocalories }
        }.getOrDefault(0.0)

        val steps = runCatching {
            c.readRecords(ReadRecordsRequest(StepsRecord::class, range))
                .records.sumOf { it.count.toLong() }
        }.getOrDefault(0L)

        val distance = runCatching {
            c.readRecords(ReadRecordsRequest(DistanceRecord::class, range))
                .records.sumOf { it.distance.inMeters }
        }.getOrDefault(0.0)

        val exerciseMin = runCatching {
            val rec = c.readRecords(ReadRecordsRequest(ExerciseSessionRecord::class, range)).records
            rec.sumOf { java.time.Duration.between(it.startTime, it.endTime).toMinutes() }
        }.getOrDefault(0L)

        return HcReading(
            activeKcal = activeKcal.toInt(),
            totalKcal = totalKcal.toInt(),
            steps = steps.toInt(),
            distanceMeters = distance.toInt(),
            exerciseMinutes = exerciseMin.toInt(),
            granted = true,
            status = st,
            lastUpdatedMs = System.currentTimeMillis()
        )
    }
}
