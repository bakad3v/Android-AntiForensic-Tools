package com.android.aftools.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class ButtonSettings(val triggerOnButton: Boolean = false,val latency: Int = 1000, val allowedClicks: Int=5)
