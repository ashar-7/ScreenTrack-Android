package com.se7en.screentrack

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen

class ScreenTrackApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AndroidThreeTen.init(this)
    }
}
