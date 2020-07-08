package com.se7en.screentrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.models.Day

@Database(entities = [Day::class, DayStats::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun statsDao(): StatsDao
}
