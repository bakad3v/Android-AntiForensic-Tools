package com.sonozaki.triggerreceivers.services.domain.repository

import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSelected
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow

interface ReceiversRepository {
    suspend fun getDeviceProtectionSettings(): DeviceProtectionSettings
    suspend fun getPasswordStatus(): Boolean
    suspend fun getUsbSettings(): UsbSettings
    suspend fun getSettings(): Settings
    suspend fun getPermissions(): Permissions
    suspend fun getButtonSettings(): ButtonSettings
    suspend fun getButtonClicksData(buttonSelected: ButtonSelected): ButtonClicksData
    fun getButtonSettingsFlow(): Flow<ButtonSettings>
    fun getPermissionsFlow(): Flow<Permissions>
    suspend fun onRightPassword()
    suspend fun onWrongPassword(): Boolean
    suspend fun setAdminActive(status: Boolean)
    suspend fun checkPassword(password: CharArray): Boolean
    suspend fun setServiceStatus(status: Boolean)
    suspend fun setRunOnBoot(status: Boolean)
    suspend fun setClicksInRow(clicks: Int, buttonSelected: ButtonSelected)
    suspend fun setLastTimestamp(timestamp: Long, buttonSelected: ButtonSelected)
    suspend fun writeToLogs(text: String)
    suspend fun areLogsEnabled(): Boolean
}