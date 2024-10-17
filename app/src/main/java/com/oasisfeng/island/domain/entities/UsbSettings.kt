package com.oasisfeng.island.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class UsbSettings(val runOnConnection: Boolean=false)