package com.android.aftools.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class AppDatastore(
    val packageName: String,
    val appName: String,
    val system: Boolean,
    val enabled: Boolean,
    val toDelete: Boolean,
    val toHide: Boolean,
    val toClearData: Boolean
)