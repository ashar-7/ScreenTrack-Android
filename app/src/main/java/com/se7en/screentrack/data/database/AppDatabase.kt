package com.se7en.screentrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.models.Day

@Database(entities = [Day::class, DayStats::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun statsDao(): StatsDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app-database"
                ).build().also { instance = it }
            }
        }
    }
}
