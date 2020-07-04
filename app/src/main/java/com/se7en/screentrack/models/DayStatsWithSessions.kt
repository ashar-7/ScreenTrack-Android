package com.se7en.screentrack.models

import androidx.room.Embedded
import androidx.room.Relation

data class DayStatsWithSessions(
    @Embedded val dayStats: DayStats,
    @Relation(
        entity = Session::class,
        parentColumn = "dateStartMillis",
        entityColumn = "dayStatsId"
    )
    val sessions: List<Session>
)
