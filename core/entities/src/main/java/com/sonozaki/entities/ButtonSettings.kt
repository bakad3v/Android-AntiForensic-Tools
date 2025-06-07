package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class ButtonSettings(val triggerOnButton: Boolean = false, val latencyUsualMode: Int = 1500, val allowedClicks: Int=5, val latencyRootMode: Int = 500)
