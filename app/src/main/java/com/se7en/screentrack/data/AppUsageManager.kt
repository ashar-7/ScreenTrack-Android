package com.se7en.screentrack.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.se7en.screentrack.Utils
import com.se7en.screentrack.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.max


class AppUsageManager(
    private val context: Context,
    private val usageStatsManager: UsageStatsManager?
) {

    enum class FILTER { TODAY, THIS_WEEK }

    suspend fun getUsageList(dayWithStats: DayWithDayStats): List<AppUsage> {
        return withContext(Dispatchers.Default) {
            val usageList = arrayListOf<AppUsage>()

            dayWithStats.dayStats.forEach {
                val app = App.fromContext(context, it.packageName)
                usageList.add(
                    AppUsage(
                        app,
                        it.totalTime,
                        it.lastUsed
                    )
                )
            }

            return@withContext usageList.sortedByDescending { it.totalTime }
        }
    }

    suspend fun getUsageList(daysWithStats: List<DayWithDayStats>): List<AppUsage> {
        return withContext(Dispatchers.Default) {
            val statsMap = mutableMapOf<String, AppUsage>()

            daysWithStats.forEach {
                for (stats in it.dayStats) {
                    (statsMap[stats.packageName] ?: AppUsage(
                        App.fromContext(context, stats.packageName), 0L, 0L
                    )).let { usage ->
                        usage.totalTime += stats.totalTime
                        usage.lastUsed = max(stats.lastUsed, usage.lastUsed)
                        statsMap[stats.packageName] = usage
                    }
                }
            }

            return@withContext statsMap.values.sortedByDescending { it.totalTime }
        }
    }

    suspend fun getDayWithStatsForWeek(): List<DayWithDayStats> {
        return withContext(Dispatchers.IO) {
            val now = ZonedDateTime.now(ZoneId.systemDefault())
            val nowLocalDate = now.toLocalDate()
            val appsWithDayStats = arrayListOf<DayWithDayStats>()

            for (i in 0..6) {
                val date =
                    nowLocalDate.minusDays(i.toLong()).atStartOfDay(ZoneId.systemDefault())

                appsWithDayStats.add(getDayWithStats(date))
            }

            return@withContext appsWithDayStats
        }
    }

    private fun getDayWithStats(
        date: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    ): DayWithDayStats {
        val statsList = arrayListOf<DayStats>()

        val timeStartMillis = Utils.getStartOfDayMillis(date)
        val todayDate = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
        val timeEndMillis = if (date.truncatedTo(ChronoUnit.DAYS).isEqual(todayDate)) {
            System.currentTimeMillis()
        } else Utils.getStartOfDayMillis(date.plusDays(1))

        if (usageStatsManager != null) {
            val eventsMap = mutableMapOf<String, MutableList<UsageEvents.Event>>()
            val events = usageStatsManager.queryEvents(timeStartMillis, timeEndMillis)
            while (events.hasNextEvent()) {
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
                    if (pm.getLaunchIntentForPackage(packageName) != null) {
//                        val sessions = arrayListOf<Session>()
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
//                                val session = Session(
//                                    UUID.randomUUID().toString(),
//                                    startTime,
//                                    endTime,
//                                    packageName
//                                )
//
//                                sessions.add(session)

                                totalTime += endTime - startTime
                                startTime = 0L; endTime = 0L
                            }
                        }

                        // If the end time was not found, it's likely that the app is still running
                        // so assume the end time to be now
                        if (startTime != 0L && endTime == 0L) {
                            // we have a session
//                            val session = Session(
//                                UUID.randomUUID().toString(),
//                                startTime,
//                                endTime,
//                                packageName
//                            )
//
//                            sessions.add(session)

                            lastUsed = timeEndMillis
                            totalTime += lastUsed - startTime
                        }

                        // If total time is more than 1 second
                        if (totalTime >= 1000) {
                            try {
                                val stats = DayStats(
                                    packageName,
                                    totalTime,
                                    lastUsed,
                                    date
                                )

                                statsList.add(stats)
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

        return DayWithDayStats(
            Day(
                date, System.currentTimeMillis()
            ),
            statsList
        )
    }
}
