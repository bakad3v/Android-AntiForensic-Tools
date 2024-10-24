package com.android.aftools.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class LogDatastore (
    val id: Int = 0,
    val date: Long,
    val day: Long,
    val entry: String
)
