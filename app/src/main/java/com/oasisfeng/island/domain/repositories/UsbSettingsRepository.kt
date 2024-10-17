package com.oasisfeng.island.domain.repositories

import com.oasisfeng.island.domain.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface UsbSettingsRepository {
    val usbSettings: Flow<UsbSettings>
    suspend fun setUsbConnectionStatus(status: Boolean)
}