package com.sonozaki.settings.domain.repository

import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.NotificationSettings
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.VolumeButtonTriggerOptions
import kotlinx.coroutines.flow.Flow

interface SettingsScreenRepository {
    val settings: Flow<Settings>
    val permissions: Flow<Permissions>
    val usbSettings: Flow<UsbSettings>
    val bruteForceSettings: Flow<BruteforceSettings>
    val buttonSettings: Flow<ButtonSettings>
    val deviceProtectionSettings: Flow<DeviceProtectionSettings>

    val notificationSettings: Flow<NotificationSettings>

    suspend fun sendBroadcast(status: Boolean)
    suspend fun setClearItself(status: Boolean)
    suspend fun setClearData(status: Boolean)
    suspend fun setDeleteApps(new: Boolean)
    suspend fun setDeleteFiles(new: Boolean)
    suspend fun setHide(status: Boolean)
    suspend fun setLogdOnBoot(new: Boolean)
    suspend fun setLogdOnStart(new: Boolean)
    suspend fun setRemoveItself(new: Boolean)
    suspend fun setRunOnDuressPassword(status: Boolean)
    suspend fun setRunRoot(new: Boolean)
    suspend fun setScreenshotsStatus(status: Boolean)
    suspend fun setTheme(theme: Theme)
    suspend fun setTRIM(new: Boolean)
    suspend fun setWipe(new: Boolean)
    suspend fun setOwnerStatus(active: Boolean)
    suspend fun setRootStatus(status: Boolean)
    suspend fun setUsbSettings(settings: UsbSettings)
    suspend fun setBruteforceLimit(limit: Int)
    suspend fun setBruteforceStatus(status: BruteforceDetectingMethod)
    suspend fun updateLatency(latency: Int)
    suspend fun updateRootLatency(latency: Int)
    suspend fun updateAllowedClicks(allowedClicks: Int)
    suspend fun setTriggerOnButtonStatus(status: PowerButtonTriggerOptions)
    suspend fun setTriggerOnVolumeButtonStatus(status: VolumeButtonTriggerOptions)
    suspend fun setTriggerOnVolumeButtonLatency(latency: Int)
    suspend fun setVolumeButtonAllowedClicks(clicks: Int)
    suspend fun changeRebootDelay(delay: Int)
    suspend fun changeMultiuserUIProtection(multiuserUIProtection: MultiuserUIProtection)
    suspend fun changeRebootOnLockStatus(status: Boolean)
}