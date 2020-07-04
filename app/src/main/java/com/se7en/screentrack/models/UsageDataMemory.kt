package com.se7en.screentrack.models

import androidx.room.PrimaryKey
import com.se7en.screentrack.data.AppUsageManager

data class UsageDataMemory(
    @PrimaryKey val filter: AppUsageManager.FILTER,
    val usageStats: List<AppUsageStats>,
    val lastUpdated: Long
)
