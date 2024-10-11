package com.android.aftools.domain.repositories

import com.android.aftools.domain.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface UsbSettingsRepository {
    val usbSettings: Flow<UsbSettings>
    suspend fun setUsbConnectionStatus(status: Boolean)
}