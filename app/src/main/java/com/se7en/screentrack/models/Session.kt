package com.se7en.screentrack.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Session(
    @PrimaryKey val sessionId: String,
    val startMillis: Long,
    val endMillis: Long,
    val appId: String
)
