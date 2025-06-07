package com.android.aftools.adapters

import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.data.settings.repositories.BruteforceRepository
import com.sonozaki.data.settings.repositories.ButtonSettingsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepository
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.data.settings.repositories.UsbSettingsRepository
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import com.sonozaki.entities.UsbSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import javax.inject.Inject

class SettingsAdapter @Inject constructor(
    private val usbSettingsRepository: UsbSettingsRepository,
    private val settingsRepository: SettingsRepository,
    private val buttonSettingsRepository: ButtonSettingsRepository,
    private val bruteforceRepository: BruteforceRepository,
    private val permissionsRepository: PermissionsRepository,
    private val deviceProtectionSettingsRepository: DeviceProtectionSettingsRepository,
    private val appUpdateRepository: AppUpdateRepository
): SettingsScreenRepository {
    override val settings: Flow<Settings>
        get() = settingsRepository.settings
    override val permissions: Flow<Permissions>
        get() = permissionsRepository.permissions
    override val usbSettings: Flow<UsbSettings>
        get() = usbSettingsRepository.usbSettings
    override val bruteForceSettings: Flow<BruteforceSettings>
        get() = bruteforceRepository.bruteforceSettings
    override val buttonSettings: Flow<ButtonSettings>
        get() = buttonSettingsRepository.buttonSettings
    override val deviceProtectionSettings: Flow<DeviceProtectionSettings>
        get() = deviceProtectionSettingsRepository.deviceProtectionSettings
    override val showUpdatePopup: Flow<Boolean>
        get() = appUpdateRepository.showUpdatePopupStatus

    override suspend fun setTheme(theme: Theme) {
        settingsRepository.setTheme(theme)
    }

    override suspend fun setDeleteApps(new: Boolean) {
        settingsRepository.setDeleteApps(new)
    }

    override suspend fun setDeleteFiles(new: Boolean) {
        settingsRepository.setDeleteFiles(new)
    }

    override suspend fun setTRIM(new: Boolean) {
        settingsRepository.setTRIM(new)
    }

    override suspend fun setWipe(new: Boolean) {
        settingsRepository.setWipe(new)
    }

    override suspend fun setOwnerStatus(active: Boolean) {
        permissionsRepository.setOwnerStatus(active)
    }

    override suspend fun setRootStatus(status: Boolean) {
        permissionsRepository.setRootStatus(status)
    }

    override suspend fun setUsbSettings(settings: UsbSettings) {
        usbSettingsRepository.setUsbSettings(settings)
    }

    override suspend fun setBruteforceLimit(limit: Int) {
        bruteforceRepository.setBruteforceLimit(limit)
    }

    override suspend fun setBruteforceStatus(status: Boolean) {
        bruteforceRepository.setBruteforceStatus(status)
    }

    override suspend fun setRunRoot(new: Boolean) {
        settingsRepository.setRunRoot(new)
    }

    override suspend fun sendBroadcast(status: Boolean) {
        settingsRepository.sendBroadcast(status)
    }

    override suspend fun setRemoveItself(new: Boolean) {
        settingsRepository.setRemoveItself(new)
    }

    override suspend fun setLogdOnStart(new: Boolean) {
        settingsRepository.setLogdOnStart(new)
    }

    override suspend fun setLogdOnBoot(new: Boolean) {
        settingsRepository.setLogdOnBoot(new)
    }

    override suspend fun setHide(status: Boolean) {
        settingsRepository.setHide(status)
    }

    override suspend fun setClearData(status: Boolean) {
        settingsRepository.setClearData(status)
    }

    override suspend fun setClearItself(status: Boolean) {
        settingsRepository.setClearItself(status)
    }

    override suspend fun setUserLimit(limit: Int) {
        settingsRepository.setUserLimit(limit)
    }

    override suspend fun getUserLimit(): Int? {
        return settingsRepository.getUserLimit()
    }

    override suspend fun setRunOnDuressPassword(status: Boolean) {
       settingsRepository.setRunOnDuressPassword(status)
    }

    override suspend fun setMultiuserUIStatus(status: Boolean) {
        settingsRepository.setMultiuserUIStatus(status)
    }

    override suspend fun setSafeBootStatus(status: Boolean) {
        settingsRepository.setSafeBootStatus(status)
    }

    override suspend fun getSafeBootStatus(): Boolean {
        return settingsRepository.getSafeBootStatus()
    }

    override suspend fun setSwitchUserRestriction(status: Boolean) {
        settingsRepository.setSwitchUserRestriction(status)
    }

    override suspend fun getSwitchUserRestriction(): Boolean {
        return settingsRepository.getSwitchUserRestriction()
    }

    override suspend fun getUserSwitcherStatus(): Boolean {
        return settingsRepository.getUserSwitcherStatus()
    }

    override suspend fun setUserSwitcherStatus(status: Boolean) {
        settingsRepository.setUserSwitcherStatus(status)
    }

    override suspend fun getMultiuserUIStatus(): Boolean {
        return settingsRepository.getMultiuserUIStatus()
    }

    override suspend fun updateLatency(latency: Int) {
        buttonSettingsRepository.updateLatency(latency)
    }

    override suspend fun updateRootLatency(latency: Int) {
        buttonSettingsRepository.updateRootLatency(latency)
    }

    override suspend fun updateAllowedClicks(allowedClicks: Int) {
        buttonSettingsRepository.updateAllowedClicks(allowedClicks)
    }

    override suspend fun setTriggerOnButtonStatus(status: Boolean) {
        buttonSettingsRepository.setTriggerOnButtonStatus(status)
    }

    override suspend fun changeRebootDelay(delay: Int) {
        deviceProtectionSettingsRepository.changeRebootDelay(delay)
    }

    override suspend fun changeMultiuserUIProtection(multiuserUIProtection: MultiuserUIProtection) {
        deviceProtectionSettingsRepository.changeMultiuserUIProtection(multiuserUIProtection)
    }

    override suspend fun changeRebootOnLockStatus(status: Boolean) {
        deviceProtectionSettingsRepository.changeRebootOnLockStatus(status)
    }

    override suspend fun setScreenshotsStatus(status: Boolean) {
        settingsRepository.setScreenshotsStatus(status)
    }

    override suspend fun disableUpdatePopup() {
        appUpdateRepository.disableUpdatePopup()
    }

    override suspend fun downloadUpdate(): RequestResult<ResponseBody> {
        return appUpdateRepository.downloadUpdate()
    }
}