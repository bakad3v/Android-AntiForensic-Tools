package com.sonozaki.data.settings.entities

import kotlinx.serialization.Serializable

@Serializable
data class ButtonSettingsV1(val triggerOnButton: Boolean = false,val latency: Int = 1500, val allowedClicks: Int=5)
