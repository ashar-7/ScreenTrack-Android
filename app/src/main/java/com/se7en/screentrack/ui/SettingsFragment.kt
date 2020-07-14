package com.se7en.screentrack.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.se7en.screentrack.R
import com.se7en.screentrack.SettingsManager
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        themeSpinner.setItems(SettingsManager.themes)
        themeSpinner.setOnItemSelectedListener { _, _, _, item ->
            SettingsManager.setTheme(item.toString(), getSharedPreferences())
        }

        themeSpinner.selectedIndex = SettingsManager.themes.indexOf(
            SettingsManager.getCurrentTheme(getSharedPreferences()).toString()
        )
    }

    private fun getSharedPreferences() = activity?.getSharedPreferences(
        getString(R.string.app_shared_prefs_name), Context.MODE_PRIVATE
    )
}
