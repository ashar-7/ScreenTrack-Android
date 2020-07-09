package com.se7en.screentrack.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.DayStats
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class AppDetailRepository private constructor(
    db: AppDatabase
) {
    private val statsDao = db.statsDao()

    fun getDayStats(packageName: String): LiveData<List<DayStats>> {
        return statsDao.getDayStats(packageName).filterNotNull().asLiveData()
    }

    companion object {
        @Volatile private var instance: AppDetailRepository? = null

        fun getInstance(db: AppDatabase): AppDetailRepository {
            return instance ?: synchronized(this) {
                instance ?: AppDetailRepository(db).also { instance = it }
            }
        }
    }
}
