package com.se7en.screentrack.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.se7en.screentrack.Constants
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.AppStatsModel


class Converters {

    @TypeConverter
    fun fromFilter(value: AppUsageManager.FILTER): Int {
        return when(value) {
            AppUsageManager.FILTER.TODAY -> Constants.FILTER_TODAY
            AppUsageManager.FILTER.LAST_7_DAYS -> Constants.FILTER_LAST_7_DAYS
        }
    }

    @TypeConverter
    fun intToFilter(value: Int): AppUsageManager.FILTER {
        return when(value) {
            Constants.FILTER_TODAY -> AppUsageManager.FILTER.TODAY
            Constants.FILTER_LAST_7_DAYS -> AppUsageManager.FILTER.LAST_7_DAYS

            else -> throw Exception("Unknown Filter integer: $value")
        }
    }

    @TypeConverter
    fun stringToStats(json: String): List<AppStatsModel> {
        return Gson().fromJson(
            json,
            object : TypeToken<List<AppStatsModel>>() {}.type
        )
    }

    @TypeConverter
    fun statsToString(stats: List<AppStatsModel>): String {
        return Gson().toJson(
            stats
        )
    }
}
