package com.se7en.screentrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.se7en.screentrack.models.App
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.models.Session
import com.se7en.screentrack.models.UsageData

@Database(entities = [UsageData::class, App::class, Session::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun usageDao(): UsageDao
    abstract fun statsDao(): StatsDao
}
