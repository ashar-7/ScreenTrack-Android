package com.se7en.screentrack.models

import androidx.room.Embedded
import androidx.room.Relation

data class AppWithSessions(
    @Embedded val app: App,
    @Relation(
        entity = Session::class,
        parentColumn = "packageName",
        entityColumn = "appId"
    )
    val sessions: List<Session>
)
