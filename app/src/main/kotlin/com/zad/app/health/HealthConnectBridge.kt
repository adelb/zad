package com.zad.app.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.ZoneId

enum class HcStatus { AVAILABLE, NEEDS_UPDATE, NOT_INSTALLED }

data class HcReading(
    val activeKcal: Int = 0,
    val steps: Int = 0,
    val granted: Boolean = false,
    val status: HcStatus = HcStatus.NOT_INSTALLED
)

/**
 * Thin wrapper around Health Connect for reading active calories + steps
 * from whatever health source the user has connected (Samsung Health,
 * Fitbit, Google Fit, Mi Fitness, …). Huawei Health does not natively
 * write to Health Connect on most devices — see release notes.
 */
class HealthConnectBridge(private val context: Context) {

    val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    fun status(): HcStatus = when (HealthConnectClient.getSdkStatus(context)) {
        HealthConnectClient.SDK_AVAILABLE -> HcStatus.AVAILABLE
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HcStatus.NEEDS_UPDATE
        else -> HcStatus.NOT_INSTALLED
    }

    private fun client(): HealthConnectClient? =
        if (status() == HcStatus.AVAILABLE) HealthConnectClient.getOrCreate(context) else null

    /** Build the rememberLauncherForActivityResult contract for requesting our permissions. */
    fun permissionContract() = PermissionController.createRequestPermissionResultContract()

    suspend fun hasPermissions(): Boolean {
        val c = client() ?: return false
        return c.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    /** Today's active calories + steps. Returns zeros if no permission / not available. */
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

        val kcal = runCatching {
            c.readRecords(ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, range))
                .records.sumOf { it.energy.inKilocalories }
        }.getOrDefault(0.0)

        val steps = runCatching {
            c.readRecords(ReadRecordsRequest(StepsRecord::class, range))
                .records.sumOf { it.count.toLong() }
        }.getOrDefault(0L)

        return HcReading(
            activeKcal = kcal.toInt(),
            steps = steps.toInt(),
            granted = true,
            status = st
        )
    }
}
