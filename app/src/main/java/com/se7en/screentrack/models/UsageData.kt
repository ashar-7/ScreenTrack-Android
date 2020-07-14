package com.se7en.screentrack.models

import com.se7en.screentrack.data.AppUsageManager

data class UsageData(
    val filter: AppUsageManager.FILTER,
    val usageList: List<AppUsage>
)
