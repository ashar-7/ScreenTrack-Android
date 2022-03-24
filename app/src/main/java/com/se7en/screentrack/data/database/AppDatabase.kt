package com.se7en.screentrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.se7en.screentrack.data.database.entity.Day
import com.se7en.screentrack.data.database.entity.DayStats
import com.se7en.screentrack.data.database.entity.TimeLimit

@Database(
    entities = [Day::class, DayStats::class, TimeLimit::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao
    abstract fun timeLimitDao(): TimeLimitDao
}
