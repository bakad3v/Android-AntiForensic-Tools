package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class BruteforceSettings(
    val wrongAttempts: Int = 0,
    val allowedAttempts: Int = 10,
    val detectingMethod: BruteforceDetectingMethod = BruteforceDetectingMethod.NONE
)