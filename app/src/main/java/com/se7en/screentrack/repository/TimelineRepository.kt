package com.se7en.screentrack.repository

import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.Session
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject


class TimelineRepository @Inject constructor(
    private val appUsageManager: AppUsageManager
) {

    suspend fun getSessions(date: ZonedDateTime): List<Session> {
        return appUsageManager.getSessions(date)
    }

}
