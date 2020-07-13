package com.se7en.screentrack.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.se7en.screentrack.Utils
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.AppDatabase
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
                    // remove this guy
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
//        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
//        statsDao.upsert(listOf(appUsageManager.getDayWithStats(today)))
        statsDao.upsert(appUsageManager.getDayWithStatsForWeek())
        // get stats for today first and upsert it, then get stats for rest of the days
    }
}
// TODO: display progress bar when updating
