package com.se7en.screentrack.service

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.se7en.screentrack.repository.HomeRepository
import com.se7en.screentrack.repository.TimeLimitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class CurrentAppService : AccessibilityService() {

    @Inject
    lateinit var timeLimitRepository: TimeLimitRepository

    @Inject
    lateinit var usageRepository: HomeRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val TAG = "CurrentAppService"
    }

    private var currentFocusedPackage = ""

    override fun onServiceConnected() {
        Log.d(TAG, "Service Connected!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Accessibility Event!")
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.packageName != null && event.className != null) {
                val componentName =
                    ComponentName(event.packageName.toString(), event.className.toString())
                val activityInfo = getActivityName(componentName)
                activityInfo?.let { info ->
                    val tempPackageName = info.packageName.trim()
                    if (tempPackageName.isNotEmpty() && currentFocusedPackage != tempPackageName) {
                        Log.d(TAG, "Current package: $tempPackageName")
                        currentFocusedPackage = tempPackageName

                        serviceScope.launch {
                            if (isTimeLimitExceeded(currentFocusedPackage)) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Time Limit Exceeded",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun isTimeLimitExceeded(pkg: String): Boolean {
        val timeLimit = timeLimitRepository.getTimeLimitBlocking(pkg)
        val todayData = usageRepository.getTodayUsageData().first()
        val usage = todayData.usageList.find { it.app.packageName == timeLimit?.packageName }
        return if (timeLimit != null && usage != null) {
            usage.totalTime > (timeLimit.hour + timeLimit.minute)
        } else false
    }

    private fun getActivityName(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            // e.printStackTrace()
            null
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service Interrupted!")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
