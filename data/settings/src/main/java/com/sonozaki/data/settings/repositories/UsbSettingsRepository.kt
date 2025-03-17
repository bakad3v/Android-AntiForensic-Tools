package com.sonozaki.data.settings.repositories

import com.sonozaki.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface UsbSettingsRepository {
    val usbSettings: Flow<UsbSettings>
    suspend fun setUsbSettings(settings: UsbSettings)
}