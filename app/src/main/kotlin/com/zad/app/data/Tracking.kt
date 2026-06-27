package com.zad.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordedAtMs: Long,
    val dayKey: String,
    val kg: Double
)

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordedAtMs: Long,
    val dayKey: String,
    val ml: Int
)
