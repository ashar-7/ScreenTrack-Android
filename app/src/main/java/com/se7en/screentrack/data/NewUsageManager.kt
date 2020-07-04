package com.se7en.screentrack.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.se7en.screentrack.models.*
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*


class NewUsageManager(
    private val context: Context,
    private val usageStatsManager: UsageStatsManager?
) {

    // use this if no usageData present in database
    fun getUsageData(filter: AppUsageManager.FILTER): AppsUsageData {
        val pair = getUsageStatsByFilter(filter)
        val lastUpdatedMillis = pair.first
        val appsWithSessions = pair.second

        return AppsUsageData(
            filter,
            appsWithSessions.sortedByDescending { it.app.totalUsedMillis },
            lastUpdatedMillis
        )
    }

    // use this if usageData is available
    fun getUsageData(oldUsageData: AppsUsageData): AppsUsageData {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val pair = getUsageStats(oldUsageData.lastUpdated, now.toInstant().toEpochMilli())
        val lastUpdatedMillis = pair.first
        val newUsageStats = pair.second

        val updatedAppsWithSessions = mutableMapOf<String, AppWithSessions>()

        // old usage stats
        oldUsageData.appsWithSessions.forEach {
            it.app.setDrawableIfNull(context)
            updatedAppsWithSessions[it.app.packageName] = it
        }

        // new usage stats
        newUsageStats.forEach { newStats ->
            val packageName = newStats.app.packageName
            val oldStats = updatedAppsWithSessions[packageName]
            updatedAppsWithSessions[packageName] = when {
                oldStats != null -> {
                    // found in old stats
                    val lastUsed = newStats.app.lastUsedMillis
                    val totalUsed = oldStats.app.totalUsedMillis + newStats.app.totalUsedMillis
                    val sessions = oldStats.sessions + newStats.sessions

                    AppWithSessions(
                        App.fromContext(context, packageName, totalUsed, lastUsed),
                        sessions
                    )
                }
                else -> {
                    // new package (not found in old stats)
                    newStats.app.setDrawableIfNull(context)
                    newStats
                }
            }
        }

        return AppsUsageData(
            oldUsageData.filter,
            updatedAppsWithSessions.values.sortedByDescending { it.app.totalUsedMillis },
            lastUpdatedMillis
        )
    }

    private fun getUsageStatsByFilter(filter: AppUsageManager.FILTER)
            : Pair<Long, List<AppWithSessions>> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val timeStart = when(filter) {
            AppUsageManager.FILTER.TODAY -> now.toLocalDate().atStartOfDay(ZoneId.systemDefault())
            AppUsageManager.FILTER.LAST_7_DAYS -> now.toLocalDate().minusDays(7).atStartOfDay(ZoneId.systemDefault())
        }

        val timeStartMillis = timeStart.toInstant().toEpochMilli()
        val timeEndMillis = now.toInstant().toEpochMilli()

        return getUsageStats(timeStartMillis, timeEndMillis)
    }

    private fun getUsageStats(
        timeStartMillis: Long,
        timeEndMillis: Long
    ): Pair<Long, List<AppWithSessions>> {
        val appWithSessionsList = arrayListOf<AppWithSessions>()

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
                        val sessions = arrayListOf<Session>()

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
                                // we have a session
                                val session = Session(
                                    UUID.randomUUID().toString(),
                                    startTime,
                                    endTime,
                                    packageName
                                )

                                sessions.add(session)

                                totalTime += endTime - startTime
                                startTime = 0L; endTime = 0L
                            }
                        }

                        // If the end time was not found, it's likely that the app is still running
                        // so assume the end time to be now
                        if (startTime != 0L && endTime == 0L) {
                            // we have a session
                            val session = Session(
                                UUID.randomUUID().toString(),
                                startTime,
                                endTime,
                                packageName
                            )

                            sessions.add(session)

                            lastUsed = timeEndMillis
                            totalTime += lastUsed - startTime
                        }

                        // If total time is more than 1 second
                        if (totalTime >= 1000) {
                            try {
                                val appIcon = pm.getApplicationIcon(appInfo)
                                val appName = pm.getApplicationLabel(appInfo)

                                val app = App(
                                    packageName,
                                    appName.toString(),
                                    totalTime,
                                    lastUsed,
                                    appIcon
                                )

                                appWithSessionsList.add(AppWithSessions(app, sessions))
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

        return Pair(timeEndMillis, appWithSessionsList)
    }
}
