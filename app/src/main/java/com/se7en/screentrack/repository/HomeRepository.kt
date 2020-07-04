package com.se7en.screentrack.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.UsageDao
import com.se7en.screentrack.models.AppStatsModel
import com.se7en.screentrack.models.UsageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class HomeRepository private constructor(
    private val usageDao: UsageDao,
    private val appUsageManager: AppUsageManager
) {

    val todayUsageData = MutableLiveData<UsageData>()
    val last7DaysUsageData = MutableLiveData<UsageData>()

    suspend fun fetchUsageData(filter: AppUsageManager.FILTER) =
        withContext(Dispatchers.IO) {
            val usageData = usageDao.getUsage(filter)

            var shouldDelete = false
            val updatedUsageData = when (usageData) {
                null -> appUsageManager.getUsageData(filter)
                else -> {
                    if(isDataOutdated(usageData)) {
                        shouldDelete = true
                        appUsageManager.getUsageData(filter)
                    }
                    else
                        appUsageManager.getUsageData(usageData)
                }
            }

            when(filter) {
                AppUsageManager.FILTER.TODAY -> todayUsageData.postValue(updatedUsageData)
                AppUsageManager.FILTER.LAST_7_DAYS -> last7DaysUsageData.postValue(updatedUsageData)
            }

            // insert or update
            if(usageData == null) {
                insert(updatedUsageData)
                Log.d("HomeRepository", "inserted")
            } else {
                if(shouldDelete) {
                    delete(usageData)
                    insert(updatedUsageData)
                    Log.d("HomeRepository", "deleted and inserted")
                } else {
                    update(updatedUsageData)
                    Log.d("HomeRepository", "updated")
                }
            }
        }

    private fun isDataOutdated(usageData: UsageData): Boolean {
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val todayStartMillis = now
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        if(usageData.lastUpdated < todayStartMillis) return true

        return false
    }

    private suspend fun insert(usageData: UsageData) {
        // maybe reference so make a new usagedata
        val dbStatsList = usageData.usageStats.map {
            AppStatsModel(
                it.packageName,
                it.totalTimeMillis,
                it.lastUsedMillis,
                it.appName,
                it.totalTimeString,
                null
            )
        }
        usageDao.insert(UsageData(usageData.filter, dbStatsList, usageData.lastUpdated))
    }

    private suspend fun update(usageData: UsageData) {
        val dbStatsList = usageData.usageStats.map {
            AppStatsModel(
                it.packageName,
                it.totalTimeMillis,
                it.lastUsedMillis,
                it.appName,
                it.totalTimeString,
                null
            )
        }
        usageDao.update(UsageData(usageData.filter, dbStatsList, usageData.lastUpdated))
    }

    private suspend fun delete(usageData: UsageData) {
        usageDao.delete(usageData)
    }

    companion object {
        @Volatile
        private var instance: HomeRepository? = null

        fun getInstance(usageDao: UsageDao, appUsageManager: AppUsageManager) =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(usageDao, appUsageManager).also { instance = it }
            }
    }
}
// TODO: store data date-wise in database and maybe add all sessions to the database to track
// Session (data by date) and Sub sessions (data by startTime and endTime)
// Update: Use and rename NewUsageManager