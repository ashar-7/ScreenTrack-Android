package com.se7en.screentrack.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.se7en.screentrack.Utils
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.DayWithDayStats
import com.se7en.screentrack.models.UsageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

class HomeRepository private constructor(
    db: AppDatabase,
    private val appUsageManager: AppUsageManager
) {

    private val statsDao = db.statsDao()

    val todayUsageData = MutableLiveData<UsageData>()
    val last7DaysUsageData = MutableLiveData<UsageData>()

    suspend fun fetchTodayUsageData() {
        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
        statsDao.getDayWithDayStats(today).filterNotNull().map { dayWithStats ->
            Log.d("HomeRepository", "mapping today")
            UsageData(
                AppUsageManager.FILTER.TODAY,
                appUsageManager.getUsageList(dayWithStats)
            )
        }.collect {
            todayUsageData.value = it
        }
    }

    suspend fun fetchWeekUsageData() {
        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
        val weekStart = today.minusDays(6).truncatedTo(ChronoUnit.DAYS)

        statsDao.getDaysWithDayStats().filterNotNull().map {
            Log.d("HomeRepository", "mapping week")
            it.forEach { stats ->
                if(stats.day.date.isBefore(weekStart)) {
                    // remove this guy
                    statsDao.delete(stats.day)
                }
            }

            UsageData(
                AppUsageManager.FILTER.LAST_7_DAYS,
                appUsageManager.getUsageList(it)
            )
        }.collect {
            last7DaysUsageData.value = it
        }
    }

    suspend fun updateData() {
        Log.d("HomeRepository", "updating...")
//        val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
//        statsDao.upsert(listOf(appUsageManager.getDayWithStats(today)))
        statsDao.upsert(appUsageManager.getDayWithStatsForWeek())
        // get stats for today first and upsert it, then get stats for rest of the days
    }

    companion object {
        @Volatile
        private var instance: HomeRepository? = null

        fun getInstance(db: AppDatabase, appUsageManager: AppUsageManager) =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(db, appUsageManager).also { instance = it }
            }
    }
}
// Sessions
// TODO: display progress bar when updating
