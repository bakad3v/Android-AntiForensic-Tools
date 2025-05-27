package com.sonozaki.settings.domain.repository

import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import com.sonozaki.entities.UsbSettings
import kotlinx.coroutines.flow.Flow
import com.bakasoft.network.RequestResult
import okhttp3.ResponseBody

interface SettingsScreenRepository {
    val settings: Flow<Settings>
    val permissions: Flow<Permissions>
    val usbSettings: Flow<UsbSettings>
    val bruteForceSettings: Flow<BruteforceSettings>
    val buttonSettings: Flow<ButtonSettings>
    val deviceProtectionSettings: Flow<DeviceProtectionSettings>
    val showUpdatePopup: Flow<Boolean>

    suspend fun sendBroadcast(status: Boolean)
    suspend fun setClearItself(status: Boolean)
    suspend fun setClearData(status: Boolean)
    suspend fun setDeleteApps(new: Boolean)
    suspend fun setDeleteFiles(new: Boolean)
    suspend fun setHide(status: Boolean)
    suspend fun setLogdOnBoot(new: Boolean)
    suspend fun setLogdOnStart(new: Boolean)
    suspend fun setMultiuserUIStatus(status: Boolean)
    suspend fun setRemoveItself(new: Boolean)
    suspend fun setRunOnDuressPassword(status: Boolean)
    suspend fun setRunRoot(new: Boolean)
    suspend fun setSafeBootStatus(status: Boolean)
    suspend fun setScreenshotsStatus(status: Boolean)
    suspend fun setTheme(theme: Theme)
    suspend fun setTRIM(new: Boolean)
    suspend fun setUserLimit(limit: Int)
    suspend fun setUserSwitcherStatus(status: Boolean)
    suspend fun setSwitchUserRestriction(status: Boolean)
    suspend fun setWipe(new: Boolean)
    suspend fun setOwnerStatus(active: Boolean)
    suspend fun setRootStatus(status: Boolean)
    suspend fun setUsbSettings(settings: UsbSettings)
    suspend fun setBruteforceLimit(limit: Int)
    suspend fun setBruteforceStatus(status: Boolean)
    suspend fun getUserLimit(): Int?
    suspend fun getUserSwitcherStatus(): Boolean
    suspend fun getSwitchUserRestriction(): Boolean
    suspend fun getSafeBootStatus(): Boolean
    suspend fun getMultiuserUIStatus(): Boolean
    suspend fun updateLatency(latency: Int)
    suspend fun updateAllowedClicks(allowedClicks: Int)
    suspend fun setTriggerOnButtonStatus(status: Boolean)
    suspend fun changeRebootDelay(delay: Int)
    suspend fun changeMultiuserUIProtection(multiuserUIProtection: MultiuserUIProtection)
    suspend fun changeRebootOnLockStatus(status: Boolean)
    suspend fun disableUpdatePopup()
    suspend fun downloadUpdate(): RequestResult<ResponseBody>
}