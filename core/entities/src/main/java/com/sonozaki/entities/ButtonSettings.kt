package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class ButtonSettings(
    val triggerOnButton: PowerButtonTriggerOptions = PowerButtonTriggerOptions.IGNORE,
    val latencyUsualMode: Int = 1500,
    val allowedClicks: Int = 5,
    val latencyRootMode: Int = 500,
    val triggerOnVolumeButton: VolumeButtonTriggerOptions = VolumeButtonTriggerOptions.IGNORE,
    val volumeButtonAllowedClicks: Int = 5,
    val latencyVolumeButton: Int = 500
)
