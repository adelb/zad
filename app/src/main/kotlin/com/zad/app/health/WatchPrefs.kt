package com.zad.app.health

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.watchDataStore by preferencesDataStore(name = "zad_watch")

/** Persists a manual "active calories from my watch" entry per day. */
class WatchPrefs(private val context: Context) {
    private val K_MANUAL_KCAL = intPreferencesKey("manual_kcal")
    private val K_MANUAL_STEPS = intPreferencesKey("manual_steps")
    private val K_MANUAL_DAY  = stringPreferencesKey("manual_day")
    private val K_MANUAL_AT   = longPreferencesKey("manual_at")

    fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val manualKcalToday: Flow<Int> = context.watchDataStore.data.map { p ->
        if (p[K_MANUAL_DAY] == today()) p[K_MANUAL_KCAL] ?: 0 else 0
    }
    val manualStepsToday: Flow<Int> = context.watchDataStore.data.map { p ->
        if (p[K_MANUAL_DAY] == today()) p[K_MANUAL_STEPS] ?: 0 else 0
    }

    suspend fun setManual(kcal: Int, steps: Int) {
        context.watchDataStore.edit {
            it[K_MANUAL_KCAL] = kcal
            it[K_MANUAL_STEPS] = steps
            it[K_MANUAL_DAY]  = today()
            it[K_MANUAL_AT]   = System.currentTimeMillis()
        }
    }
}
