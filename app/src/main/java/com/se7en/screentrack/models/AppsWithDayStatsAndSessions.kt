package com.se7en.screentrack.models

import androidx.room.Embedded
import androidx.room.Relation

data class AppsWithDayStatsAndSessions (
    @Embedded val app: App,
    @Relation(
        entity = DayStats::class,
        parentColumn = "packageName",
        entityColumn = "appId"
    )
    val stats: List<DayStatsWithSessions>
)
