package com.se7en.screentrack.models

data class Session(
    val startMillis: Long,
    val endMillis: Long,
    val app: App
)
