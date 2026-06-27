package com.zad.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.profileDataStore by preferencesDataStore(name = "zad_profile")

class ProfileStore(private val context: Context) {

    private val K_DONE     = booleanPreferencesKey("onboarded")
    private val K_DOB      = stringPreferencesKey("birth_date")    // ISO yyyy-MM-dd
    private val K_SEX      = stringPreferencesKey("sex")
    private val K_HEIGHT   = intPreferencesKey("height_cm")
    private val K_WEIGHT   = doublePreferencesKey("weight_kg")
    private val K_ACTIVITY = stringPreferencesKey("activity")
    private val K_GOAL     = stringPreferencesKey("goal")

    val onboarded: Flow<Boolean> = context.profileDataStore.data.map { it[K_DONE] ?: false }

    val profile: Flow<Profile?> = context.profileDataStore.data.map { p ->
        val dobStr = p[K_DOB] ?: return@map null
        val dob = runCatching { LocalDate.parse(dobStr) }.getOrNull() ?: return@map null
        val sex = runCatching { Sex.valueOf(p[K_SEX] ?: "MALE") }.getOrDefault(Sex.MALE)
        val h = p[K_HEIGHT] ?: return@map null
        val w = p[K_WEIGHT] ?: return@map null
        val act = runCatching { ActivityLevel.valueOf(p[K_ACTIVITY] ?: "MODERATE") }
            .getOrDefault(ActivityLevel.MODERATE)
        val goal = runCatching { Goal.valueOf(p[K_GOAL] ?: "MAINTAIN") }.getOrDefault(Goal.MAINTAIN)
        Profile(dob, sex, h, w, act, goal)
    }

    suspend fun save(p: Profile) {
        context.profileDataStore.edit {
            it[K_DONE]     = true
            it[K_DOB]      = p.birthDate.toString()
            it[K_SEX]      = p.sex.name
            it[K_HEIGHT]   = p.heightCm
            it[K_WEIGHT]   = p.weightKg
            it[K_ACTIVITY] = p.activity.name
            it[K_GOAL]     = p.goal.name
        }
    }
}
