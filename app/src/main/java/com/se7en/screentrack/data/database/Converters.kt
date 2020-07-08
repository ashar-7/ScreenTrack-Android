package com.se7en.screentrack.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.se7en.screentrack.Constants
import com.se7en.screentrack.Utils
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit


class Converters {

    @TypeConverter
    fun fromDate(date: ZonedDateTime): Long {
        return date.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun longToDate(millis: Long): ZonedDateTime {
        return Utils.getZonedDateTime(millis, ChronoUnit.DAYS)
    }
}
