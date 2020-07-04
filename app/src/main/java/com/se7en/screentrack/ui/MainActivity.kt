package com.se7en.screentrack.ui

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.se7en.screentrack.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.navHost)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)

        if(!hasUsageAccessPermission())
            showUsageAccessPermissionDialog()
    }

    private fun showUsageAccessPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission")
            .setMessage("In order to use the app, " +
                    "please grant the App Usage Access permission in settings")
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setPositiveButton("Okay") { _, _ ->
                requestUsageAccessPermission()
            }
            .create().show()
    }

    private fun requestUsageAccessPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        if(intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 7)
        }
    }

    private fun hasUsageAccessPermission(): Boolean {
        val appOpsManager = getSystemService(
            Context.APP_OPS_SERVICE
        ) as AppOpsManager? ?: return false

        val mode = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
            else -> appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
        }

        return mode == MODE_ALLOWED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.nightModeSwitch -> {
                if(AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                else
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                false
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
