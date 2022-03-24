package com.se7en.screentrack.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.StatsDao
import com.se7en.screentrack.data.database.entity.DayStats
import com.se7en.screentrack.models.SessionMinimal
import kotlinx.coroutines.flow.filterNotNull
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject


class AppDetailRepository @Inject constructor(
    private val statsDao: StatsDao,
    private val appUsageManager: AppUsageManager
) {

    fun getDayStats(packageName: String): LiveData<List<DayStats>> {
        return statsDao.getDayStats(packageName).filterNotNull().asLiveData()
    }

    suspend fun getSessions(packageName: String, date: ZonedDateTime): List<SessionMinimal> {
        return appUsageManager.getSessions(packageName, date)
    }
}
