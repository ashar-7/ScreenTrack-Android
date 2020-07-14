package com.se7en.screentrack

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScreenTrackApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        SettingsManager.initTheme(getSharedPreferences(getString(R.string.app_shared_prefs_name), Context.MODE_PRIVATE))
        AndroidThreeTen.init(this)
    }
}
