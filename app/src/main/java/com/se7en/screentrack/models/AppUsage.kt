package com.se7en.screentrack.models

data class AppUsage(
    val app: App,
    var totalTime: Long,
    var lastUsed: Long
)
