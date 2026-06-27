package com.zad.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MealEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ZadDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile private var INSTANCE: ZadDatabase? = null
        fun get(context: Context): ZadDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ZadDatabase::class.java,
                "zad.db"
            ).build().also { INSTANCE = it }
        }
    }
}
