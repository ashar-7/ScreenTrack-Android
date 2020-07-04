package com.se7en.screentrack.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DayStats(
    @PrimaryKey val dateStartMillis: Long, // refers to 12 AM (or whatever start of day is at a zone)
    val appId: String, // app package name
    var totalTimeMillis: Long,
    var lastUsedMillis: Long
) {
    fun setData(totalTime: Long, lastUsed: Long): DayStats {
        totalTimeMillis = totalTime
        lastUsedMillis = lastUsed
        return this
    }
}
