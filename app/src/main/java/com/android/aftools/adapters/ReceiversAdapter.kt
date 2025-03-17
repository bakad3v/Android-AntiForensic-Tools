package com.android.aftools.adapters

import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.data.settings.repositories.BruteforceRepository
import com.sonozaki.data.settings.repositories.ButtonSettingsRepository
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.data.settings.repositories.UsbSettingsRepository
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.password.repository.PasswordManager
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReceiversAdapter @Inject constructor(
    private val passwordManager: PasswordManager,
    private val usbSettingsRepository: UsbSettingsRepository,
    private val settingsRepository: SettingsRepository,
    private val buttonSettingsRepository: ButtonSettingsRepository,
    private val bruteforceRepository: BruteforceRepository,
    private val permissionsRepository: PermissionsRepository
) : ReceiversRepository {
    override suspend fun getPasswordStatus(): Boolean {
        return passwordManager.passwordStatus.first()
    }

    override suspend fun getUsbSettings(): UsbSettings {
        return usbSettingsRepository.usbSettings.first()
    }

    override suspend fun getSettings(): Settings {
        return settingsRepository.settings.first()
    }

    override suspend fun getButtonSettings(): ButtonSettings {
        return buttonSettingsRepository.buttonSettings.first()
    }

    override suspend fun getButtonClicksData(): ButtonClicksData {
        return buttonSettingsRepository.getButtonClicksData()
    }

    override suspend fun onRightPassword() {
        bruteforceRepository.onRightPassword()
    }

    override suspend fun onWrongPassword(): Boolean {
        return bruteforceRepository.onWrongPassword()
    }

    override suspend fun setAdminActive(status: Boolean) {
        permissionsRepository.setAdminStatus(status)
    }

    override suspend fun checkPassword(password: CharArray): Boolean {
        return passwordManager.checkPassword(password)
    }

    override suspend fun setServiceStatus(status: Boolean) {
        settingsRepository.setServiceStatus(status)
    }

    override suspend fun setRunOnBoot(status: Boolean) {
        settingsRepository.setRunOnBoot(status)
    }

    override suspend fun setClicksInRow(clicks: Int) {
        buttonSettingsRepository.setClicksInRow(clicks)
    }

    override suspend fun setLastTimestamp(timestamp: Long) {
        buttonSettingsRepository.setLastTimestamp(timestamp)
    }
}