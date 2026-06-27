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
    val lastUpdatedMs: Long = 0L,
    /** Packages that wrote ANY record we read today — diagnostic only. */
    val sources: Set<String> = emptySet(),
    val error: String? = null,
    /** Manual override entered by the user (e.g. typed from a Huawei watch). */
    val manualKcal: Int = 0
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
        if (status() == HcStatus.AVAILABLE)
            runCatching { HealthConnectClient.getOrCreate(context) }.getOrNull()
        else null

    fun permissionContract() = PermissionController.createRequestPermissionResultContract()

    suspend fun hasPermissions(): Boolean {
        val c = client() ?: return false
        return runCatching { c.permissionController.getGrantedPermissions() }
            .getOrDefault(emptySet()).containsAll(permissions)
    }

    /** Today's totals + the list of source apps. Never throws — captures error in HcReading.error. */
    suspend fun readToday(): HcReading {
        val st = status()
        if (st != HcStatus.AVAILABLE) return HcReading(status = st)
        val c = client() ?: return HcReading(status = st, error = "client unavailable")

        val granted = runCatching { c.permissionController.getGrantedPermissions() }
            .getOrDefault(emptySet())
        if (!granted.containsAll(permissions)) {
            return HcReading(status = st, granted = false)
        }

        val zone = ZoneId.systemDefault()
        val start = LocalDate.now().atStartOfDay(zone).toInstant()
        val end = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant()
        val range = TimeRangeFilter.between(start, end)

        val sources = mutableSetOf<String>()
        var firstError: String? = null
        fun captureError(t: Throwable) { if (firstError == null) firstError = t.message }

        var activeKcal = 0.0
        var totalKcal = 0.0
        var steps = 0L
        var distance = 0.0
        var exerciseMin = 0L

        runCatching {
            val r = c.readRecords(ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, range)).records
            activeKcal = r.sumOf { it.energy.inKilocalories }
            r.forEach { sources += it.metadata.dataOrigin.packageName }
        }.onFailure(::captureError)

        runCatching {
            val r = c.readRecords(ReadRecordsRequest(TotalCaloriesBurnedRecord::class, range)).records
            totalKcal = r.sumOf { it.energy.inKilocalories }
            r.forEach { sources += it.metadata.dataOrigin.packageName }
        }.onFailure(::captureError)

        runCatching {
            val r = c.readRecords(ReadRecordsRequest(StepsRecord::class, range)).records
            steps = r.sumOf { it.count.toLong() }
            r.forEach { sources += it.metadata.dataOrigin.packageName }
        }.onFailure(::captureError)

        runCatching {
            val r = c.readRecords(ReadRecordsRequest(DistanceRecord::class, range)).records
            distance = r.sumOf { it.distance.inMeters }
            r.forEach { sources += it.metadata.dataOrigin.packageName }
        }.onFailure(::captureError)

        runCatching {
            val r = c.readRecords(ReadRecordsRequest(ExerciseSessionRecord::class, range)).records
            exerciseMin = r.sumOf { java.time.Duration.between(it.startTime, it.endTime).toMinutes() }
            r.forEach { sources += it.metadata.dataOrigin.packageName }
        }.onFailure(::captureError)

        return HcReading(
            activeKcal = activeKcal.toInt(),
            totalKcal = totalKcal.toInt(),
            steps = steps.toInt(),
            distanceMeters = distance.toInt(),
            exerciseMinutes = exerciseMin.toInt(),
            granted = true,
            status = st,
            lastUpdatedMs = System.currentTimeMillis(),
            sources = sources,
            error = firstError
        )
    }
}
