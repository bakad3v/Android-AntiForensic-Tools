package com.sonozaki.entities

import kotlinx.serialization.Serializable

@Serializable
enum class UsbSettings {
    DO_NOTHING, RUN_ON_CONNECTION, REBOOT_ON_CONNECTION
}