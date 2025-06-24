package com.sonozaki.entities

import kotlinx.serialization.Serializable


@Serializable
data class App(
    val packageName: String,
    val appName: String,
    val system: Boolean,
    val enabled: Boolean,
    val toDelete: Boolean,
    val toHide: Boolean,
    val toClearData: Boolean
)