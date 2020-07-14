package com.se7en.screentrack

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object SettingsManager {

    private const val THEME_PREFS_KEY = "theme"

    enum class Theme {
        LIGHT {
            override fun toString() = "Light"
        },
        DARK {
            override fun toString() = "Dark"
        },
        FOLLOW_SYSTEM {
            override fun toString() = "Follow system"
        }
    }

    val themes = listOf(Theme.LIGHT.toString(), Theme.DARK.toString(), Theme.FOLLOW_SYSTEM.toString())

    fun initTheme(sharedPrefs: SharedPreferences) {
        when(sharedPrefs.getInt(THEME_PREFS_KEY, Theme.FOLLOW_SYSTEM.ordinal)) {
            Theme.LIGHT.ordinal -> setTheme(Theme.LIGHT, sharedPrefs)
            Theme.DARK.ordinal -> setTheme(Theme.DARK, sharedPrefs)
            Theme.FOLLOW_SYSTEM.ordinal -> setTheme(Theme.FOLLOW_SYSTEM, sharedPrefs)

            else -> setTheme(Theme.FOLLOW_SYSTEM, sharedPrefs)
        }
    }

    fun setTheme(theme: Theme, sharedPrefs: SharedPreferences?) {
        when(theme) {
            Theme.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Theme.DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            Theme.FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        sharedPrefs?.edit {
            putInt(THEME_PREFS_KEY, theme.ordinal)
            apply()
        }
    }

    fun setTheme(string: String, sharedPrefs: SharedPreferences?) {
        when(string) {
            Theme.LIGHT.toString() -> setTheme(Theme.LIGHT, sharedPrefs)
            Theme.DARK.toString() -> setTheme(Theme.DARK, sharedPrefs)
            Theme.FOLLOW_SYSTEM.toString() -> setTheme(Theme.FOLLOW_SYSTEM, sharedPrefs)
        }
    }

    fun getCurrentTheme(sharedPrefs: SharedPreferences?): Theme {
        return when(sharedPrefs?.getInt(THEME_PREFS_KEY, Theme.FOLLOW_SYSTEM.ordinal)) {
            Theme.LIGHT.ordinal -> Theme.LIGHT
            Theme.DARK.ordinal -> Theme.DARK
            Theme.FOLLOW_SYSTEM.ordinal -> Theme.FOLLOW_SYSTEM

            else -> Theme.FOLLOW_SYSTEM
        }
    }
}
