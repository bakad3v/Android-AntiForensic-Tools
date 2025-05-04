package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
data class DeviceProtectionSettings(val multiuserUIProtection: MultiuserUIProtection = MultiuserUIProtection.NEVER, val rebootOnLock: Boolean = false, val rebootDelay: Int = 3600)