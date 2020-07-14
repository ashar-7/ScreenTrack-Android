package com.se7en.screentrack.repository

import android.util.Log
import com.se7en.screentrack.Utils
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.StatsDao
import com.se7en.screentrack.models.UsageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject


class HomeRepository @Inject constructor(
    private val statsDao: StatsDao,
    private val appUsageManager: AppUsageManager
) {

    suspend fun getTodayUsageData(): Flow<UsageData> {
        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
        return statsDao.getDayWithDayStats(today).filterNotNull().map { dayWithStats ->
            UsageData(
                AppUsageManager.FILTER.TODAY,
                appUsageManager.getUsageList(dayWithStats)
            )
        }
    }

    suspend fun getWeekUsageData(): Flow<UsageData> {
        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
        val weekStart = today.minusDays(6).truncatedTo(ChronoUnit.DAYS)

        return statsDao.getDaysWithDayStats().filterNotNull().map {
            it.forEach { stats ->
                if(stats.day.date.isBefore(weekStart)) {
                    // remove this day's data
                    statsDao.delete(stats.day)
                }
            }

            UsageData(
                AppUsageManager.FILTER.THIS_WEEK,
                appUsageManager.getUsageList(it)
            )
        }
    }

    suspend fun updateData() {
        Log.d("HomeRepository", "updating...")
        statsDao.upsert(appUsageManager.getDayWithStatsForWeek())
    }
}
