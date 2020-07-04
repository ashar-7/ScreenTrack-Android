package com.se7en.screentrack.models

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey val packageName: String,
    val appName: String,
    val totalUsedMillis: Long,
    val lastUsedMillis: Long,
    @Ignore var iconDrawable: Drawable?
) {
    fun setDrawableIfNull(context: Context): App {
        if(iconDrawable == null) {
            val pm = context.packageManager
            iconDrawable = pm.getApplicationIcon(packageName)
        }

        return this
    }

    companion object {
        fun fromContext(
            context: Context,
            packageName: String,
            totalUsed: Long,
            lastUsed: Long
        ): App {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appIcon = pm.getApplicationIcon(info)
            val appName = pm.getApplicationLabel(info)

            return App(
                packageName,
                appName.toString(),
                totalUsed,
                lastUsed,
                appIcon
            )
        }
    }
}
