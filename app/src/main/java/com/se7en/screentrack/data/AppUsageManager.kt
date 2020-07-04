package com.se7en.screentrack.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.se7en.screentrack.Utils
import com.se7en.screentrack.models.AppStatsModel
import com.se7en.screentrack.models.UsageData
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


class AppUsageManager(
    private val context: Context,
    private val usageStatsManager: UsageStatsManager?
) {

    enum class FILTER { TODAY, LAST_7_DAYS }

    // use this if no usageData present in database
    fun getUsageData(filter: FILTER): UsageData {
        val pair = getUsageStatsByFilter(filter)
        val lastUpdatedMillis = pair.first
        val usageStats = pair.second

        return UsageData(
            filter,
            usageStats.sortedByDescending { it.totalTimeMillis },
            lastUpdatedMillis
        )
    }

    // use this if usageData is available
    fun getUsageData(oldUsageData: UsageData): UsageData {
        // handle cases if last updated is older than 24 hours or 7 days (getUsageData(usageData.filter))
        // add data somehow

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val pair = getUsageStats(oldUsageData.lastUpdated, now.toInstant().toEpochMilli())
        val lastUpdatedMillis = pair.first
        val newUsageStats = pair.second

        val updatedStats = mutableMapOf<String, AppStatsModel>()

        // old usage stats
        oldUsageData.usageStats.forEach {
            it.setDrawableIfNull(context)
            updatedStats[it.packageName] = it
        }

        // new usage stats
        newUsageStats.forEach { newStats ->
            val oldStats = updatedStats[newStats.packageName]
            updatedStats[newStats.packageName] = when {
                oldStats != null -> {
                    // found in old stats
                    val lastUsed = newStats.lastUsedMillis
                    val totalTime = oldStats.totalTimeMillis + newStats.totalTimeMillis

                    AppStatsModel.fromContext(
                        context,
                        oldStats.packageName,
                        totalTime,
                        lastUsed
                    )
                }
                else -> {
                    // new package (not found in old stats)
                    newStats.setDrawableIfNull(context)
                    newStats
                }
            }
        }

        return UsageData(
            oldUsageData.filter,
            updatedStats.values.sortedByDescending { it.totalTimeMillis },
            lastUpdatedMillis
        )
    }

    private fun getUsageStatsByFilter(filter: FILTER): Pair<Long, List<AppStatsModel>> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val timeStart = when(filter) {
            FILTER.TODAY -> now.toLocalDate().atStartOfDay(ZoneId.systemDefault())
            FILTER.LAST_7_DAYS -> now.toLocalDate().minusDays(7).atStartOfDay(ZoneId.systemDefault())
        }

        val timeStartMillis = timeStart.toInstant().toEpochMilli()
        val timeEndMillis = now.toInstant().toEpochMilli()

        return getUsageStats(timeStartMillis, timeEndMillis)
    }

    private fun getUsageStats(
        timeStartMillis: Long,
        timeEndMillis: Long
    ): Pair<Long, List<AppStatsModel>> {
        val usageStats = arrayListOf<AppStatsModel>()
        if(usageStatsManager != null) {
            val eventsMap = mutableMapOf<String, MutableList<UsageEvents.Event>>()
            val events = usageStatsManager.queryEvents(timeStartMillis, timeEndMillis)
            while(events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)
                val packageName = event.packageName

                (eventsMap[packageName] ?: mutableListOf()).let {
                    it.add(event)
                    eventsMap[packageName] = it
                }
            }

            eventsMap.forEach { (packageName, events) ->
                val pm = context.packageManager

                try {
                    val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    if (pm.getLaunchIntentForPackage(packageName) != null) {
                        var startTime = 0L
                        var endTime = 0L
                        var totalTime = 0L
                        var lastUsed = 0L
                        events.forEach { event ->
                            when (event.eventType) {
                                UsageEvents.Event.ACTIVITY_RESUMED -> { // same as MOVE_TO_FOREGROUND
                                    // start time
                                    startTime = event.timeStamp
                                }

                                UsageEvents.Event.ACTIVITY_PAUSED -> { // same as MOVE_TO_BACKGROUND
                                    // end time
                                    endTime = event.timeStamp
                                    lastUsed = endTime
                                }
                            }

                            // If an end event exists but a start event was not found,
                            // it's likely that the app was running before midnight
                            // so set startTime of the event to todayStart
                            if (startTime == 0L && endTime != 0L) startTime = timeStartMillis
                            // If both start and end times exist, add the time to totalTime
                            // and reset start and end times
                            if (startTime != 0L && endTime != 0L) {
                                totalTime += endTime - startTime
                                startTime = 0L; endTime = 0L
                            }
                        }

                        // If the end time was not found, it's likely that the app is still running
                        // so assume the end time to be now
                        if (startTime != 0L && endTime == 0L) {
                            lastUsed = timeEndMillis
                            totalTime += lastUsed - startTime - 1000
                        }

                        // If total time is more than 1 second
                        if (totalTime >= 1000) {
                            try {
                                val appIcon = pm.getApplicationIcon(appInfo)
                                val appName = pm.getApplicationLabel(appInfo)

                                val appUsageData =
                                    AppStatsModel(
                                        packageName,
                                        totalTime,
                                        lastUsed,
                                        appName.toString(),
                                        Utils.getUsageTimeString(totalTime),
                                        appIcon
                                    )
                                usageStats.add(appUsageData)
                            } catch (e: Exception) {
                                Log.d("AppUsageManager", "Failed to get info for $packageName")
                            }
                        }
                    }
                } catch (e: Exception) {
                    // TODO: also display uninstalled apps
                    Log.d("AppUsageManager", "Failed to get info for $packageName")
                }
            }
        }

        return Pair(timeEndMillis, usageStats)
    }
}
