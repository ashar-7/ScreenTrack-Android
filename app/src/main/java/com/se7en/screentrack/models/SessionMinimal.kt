package com.se7en.screentrack.models

data class SessionMinimal(
    val startMillis: Long,
    val endMillis: Long,
    val packageName: String
)
