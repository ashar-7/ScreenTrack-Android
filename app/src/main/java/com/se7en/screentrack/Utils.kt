package com.se7en.screentrack

import android.content.Context
import org.threeten.bp.Duration

object Utils {

    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

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
}