package com.se7en.screentrack

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

object Utils {

    fun getZonedDateTime(millis: Long): ZonedDateTime =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())


    fun getZonedDateTime(millis: Long, truncatedTo: ChronoUnit): ZonedDateTime =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).truncatedTo(truncatedTo)

    fun getStartOfDayMillis(date: ZonedDateTime) =
        date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun getUsageTimeString(millis: Long): String {
        var timeLeft = Duration.ofMillis(millis)
        val hours = timeLeft.toHours()

        timeLeft = timeLeft.minusHours(hours)
        val minutes = timeLeft.toMinutes()

        timeLeft = timeLeft.minusMinutes(minutes)
        val seconds = timeLeft.seconds

        return when {
            hours >= 1 -> {
                String.format("%dh %dm %ds", hours, minutes, seconds)
            }
            minutes >= 1 -> {
                String.format("%dm %ds", minutes, seconds)
            }
            else -> {
                // assuming all apps are used for at least >= 1 second
                String.format("%ds", seconds)
            }
        }
    }

    fun getLastUsedFormattedDate(millis: Long): String {
        val dateTime = getZonedDateTime(millis)
        return dateTime.format(DateTimeFormatter.ofPattern("EEE, dd MMM HH:mm:ss")).toString()
    }
}
