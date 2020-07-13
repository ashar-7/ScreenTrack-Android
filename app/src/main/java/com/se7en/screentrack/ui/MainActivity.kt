package com.se7en.screentrack.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.se7en.screentrack.R
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
                setOf(R.id.permissionFragment, R.id.homeFragment)
            ).build()
        )

        bottomNav.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(this)
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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id) {
            R.id.homeFragment -> {
                toolbar_title.text = getString(R.string.home)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.timelineFragment -> {
                toolbar_title.text = getString(R.string.timeline)
                bottomNav.visibility = View.VISIBLE
            }

            R.id.permissionFragment -> {
                toolbar_title.text = getString(R.string.app_name)
                bottomNav.visibility = View.GONE
            }

            R.id.appDetailFragment -> {
                bottomNav.visibility = View.GONE
            }
        }
    }
}
