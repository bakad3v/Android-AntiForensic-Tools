package com.sonozaki.data.settings.repositories

import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import kotlinx.coroutines.flow.Flow

interface DeviceProtectionSettingsRepository {
    val deviceProtectionSettings: Flow<DeviceProtectionSettings>
    suspend fun changeRebootDelay(delay: Int)
    suspend fun changeMultiuserUIProtection(multiuserUIProtection: MultiuserUIProtection)
    suspend fun changeRebootOnLockStatus(status: Boolean)
}