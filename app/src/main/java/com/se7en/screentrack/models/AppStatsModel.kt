package com.se7en.screentrack.models

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.se7en.screentrack.Utils

data class AppStatsModel(
    val packageName: String,
    val totalTimeMillis: Long,
    val lastUsedMillis: Long,
    val appName: String,
    val totalTimeString: String,
    var iconDrawable: Drawable?
) {
    fun setDrawableIfNull(context: Context) {
        if(iconDrawable == null) {
            val pm = context.packageManager
            iconDrawable = pm.getApplicationIcon(packageName)
        }
    }

    companion object {
        fun fromContext(
            context: Context,
            packageName: String,
            totalTimeMillis: Long,
            lastUsedMillis: Long
        ): AppStatsModel {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appIcon = pm.getApplicationIcon(info)
            val appName = pm.getApplicationLabel(info)

            return AppStatsModel(
                packageName,
                totalTimeMillis,
                lastUsedMillis,
                appName.toString(),
                Utils.getUsageTimeString(totalTimeMillis),
                appIcon
            )
        }
    }
}
