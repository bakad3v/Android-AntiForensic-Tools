package com.sonozaki.data.settings.entities

import kotlinx.serialization.Serializable

@Serializable
data class BruteforceSettingsV1(
    val bruteforceRestricted: Boolean = false,
    val wrongAttempts: Int = 0,
    val allowedAttempts: Int = 10
)