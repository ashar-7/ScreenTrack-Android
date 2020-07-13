package com.se7en.screentrack.models

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log

data class App(
    val packageName: String,
    val appName: String,
    var iconDrawable: Drawable?
) {
    fun setDrawableIfNull(context: Context): App {
        if(iconDrawable == null) {
            try {
                val pm = context.packageManager
                iconDrawable = pm.getApplicationIcon(packageName)
            } catch (e: Exception) {
                Log.d("App", "Failed to get app info for $packageName")
            }
        }

        return this
    }

    companion object {
        fun fromContext(
            context: Context,
            packageName: String
        ): App {
            var appIcon: Drawable? = null
            var appName: String
            try {
                val pm = context.packageManager
                appIcon = pm.getApplicationIcon(packageName)
                val info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                appName = pm.getApplicationLabel(info).toString()
            } catch (e: Exception) {
                appName = "$packageName (uninstalled)"
            }

            return App(
                packageName,
                appName,
                appIcon
            )
        }
    }
}
