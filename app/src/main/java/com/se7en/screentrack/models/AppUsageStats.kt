package com.se7en.screentrack.models

import android.graphics.drawable.Drawable

data class AppUsageStats(
    val packageName: String,
    val appName: String,
    val iconDrawable: Drawable,
    val totalTimeMillis: Long,
    val totalTimeString: String,
    val lastUsedMillis: Long
)
