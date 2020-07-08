package com.se7en.screentrack.models

import com.se7en.screentrack.data.AppUsageManager

data class UsageData(
    val filter: AppUsageManager.FILTER,
    val usageList: List<AppUsage>
) {
//    companion object {
//        fun fromAppsWithDayStats(
//            filter: NewUsageManager.FILTER,
//            appsWithDayStats: List<AppWithDayStats>
//        ): AppsTotalUsageData {
//            val totalStats = arrayListOf<AppStats>()
//
//            appsWithDayStats.forEach {
//                var totalTime = 0L
//                var lastUsed = 0L
//                it.dayStats.forEach { stats ->
//                    totalTime += stats.totalUsed
//                    if(lastUsed < stats.lastUsed) lastUsed = stats.lastUsed
//                }
//
//                totalStats.add(
//                    AppStats(
//                        it.app.packageName,
//                        totalTime,
//                        lastUsed
//                    )
//                )
//            }
//
//            return AppsTotalUsageData(filter, totalStats.sortedByDescending { it.totalTime })
//        }
//    }
}
