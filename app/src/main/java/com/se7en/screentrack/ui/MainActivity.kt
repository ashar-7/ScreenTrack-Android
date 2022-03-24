package com.se7en.screentrack.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.se7en.screentrack.R
import com.se7en.screentrack.service.CurrentAppService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val navController by lazy { findNavController(R.id.navHost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(
            navController,
            AppBarConfiguration.Builder(
                setOf(
                    R.id.permissionFragment,
                    R.id.homeFragment,
                    R.id.timelineFragment,
                    R.id.timeLimitListFragment,
                    R.id.settingsFragment
                )
            ).build()
        )

        bottomNav.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(this)
        if (!isServiceEnabled()) {
            promptServiceOff()
        }
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                toolbar_title.text = getString(R.string.home)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.timelineFragment -> {
                toolbar_title.text = getString(R.string.timeline)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.timeLimitListFragment -> {
                toolbar_title.text = getString(R.string.time_limits)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.settingsFragment -> {
                toolbar_title.text = getString(R.string.settings)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.permissionFragment -> {
                toolbar_title.text = getString(R.string.app_name)
                bottomNav.visibility = View.GONE
            }

            R.id.appDetailFragment -> {
                toolbar_title.text = ""
                bottomNav.visibility = View.GONE
            }
        }
    }

    private fun promptServiceOff() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Title")
            setMessage("Message")
            setPositiveButton("Enable") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
            setCancelable(false)
        }.show()
    }

    private fun isServiceEnabled(): Boolean {
        val am = applicationContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.name.contains(CurrentAppService::class.qualifiedName.toString())) {
                return true
            }
        }
        return false
    }
}
