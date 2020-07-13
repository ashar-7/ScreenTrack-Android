package com.se7en.screentrack.di

import android.content.Context
import androidx.room.Room
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.data.database.StatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object ApplicationModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides
    fun provideStatsDao(
        database: AppDatabase
    ): StatsDao {
        return database.statsDao()
    }
}
