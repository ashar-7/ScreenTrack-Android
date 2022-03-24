package com.se7en.screentrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeLimit(
    @PrimaryKey val packageName: String,
    val hour: Int,
    val minute: Int
)
