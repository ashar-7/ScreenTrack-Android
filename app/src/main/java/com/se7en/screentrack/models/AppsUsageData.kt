package com.se7en.screentrack.models

import com.se7en.screentrack.data.AppUsageManager

data class AppsUsageData(
    val filter: AppUsageManager.FILTER,
    val appsWithSessions: List<AppWithSessions>,
    val lastUpdated: Long
)
