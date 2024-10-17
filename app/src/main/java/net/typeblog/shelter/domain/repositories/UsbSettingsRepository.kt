package net.typeblog.shelter.domain.repositories

import net.typeblog.shelter.domain.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface UsbSettingsRepository {
    val usbSettings: Flow<UsbSettings>
    suspend fun setUsbConnectionStatus(status: Boolean)
}