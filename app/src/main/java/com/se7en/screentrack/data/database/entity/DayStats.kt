package com.se7en.screentrack.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import org.threeten.bp.ZonedDateTime

@Entity(
    primaryKeys = ["packageName", "dayId"],
    foreignKeys = [
        ForeignKey(
            entity = Day::class,
            parentColumns = ["date"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayStats(
    val packageName: String,
    val totalTime: Long,
    val lastUsed: Long,
    @ColumnInfo(index = true)
    val dayId: ZonedDateTime
)
