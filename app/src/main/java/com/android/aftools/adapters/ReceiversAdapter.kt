package com.android.aftools.adapters

import com.sonozaki.data.logs.repository.LogsRepository
import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.data.settings.repositories.BruteforceRepository
import com.sonozaki.data.settings.repositories.ButtonSettingsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepository
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.data.settings.repositories.UsbSettingsRepository
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSelected
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.password.repository.PasswordManager
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import kotlinx.coroutines.flow.first
import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReceiversAdapter @Inject constructor(
    private val passwordManager: PasswordManager,
    private val usbSettingsRepository: UsbSettingsRepository,
    private val settingsRepository: SettingsRepository,
    private val buttonSettingsRepository: ButtonSettingsRepository,
    private val bruteforceRepository: BruteforceRepository,
    private val permissionsRepository: PermissionsRepository,
    private val logsRepository: LogsRepository,
    private val deviceProtectionSettingsRepository: DeviceProtectionSettingsRepository
) : ReceiversRepository {
    override suspend fun getDeviceProtectionSettings(): DeviceProtectionSettings {
        return deviceProtectionSettingsRepository.deviceProtectionSettings.first()
    }

    override suspend fun getPasswordStatus(): Boolean {
        return passwordManager.passwordStatus.first()
    }

    override suspend fun getUsbSettings(): UsbSettings {
        return usbSettingsRepository.usbSettings.first()
    }

    override suspend fun getSettings(): Settings {
        return settingsRepository.settings.first()
    }

    override suspend fun getPermissions(): Permissions {
        return permissionsRepository.permissions.first()
    }

    override suspend fun getButtonSettings(): ButtonSettings {
        return buttonSettingsRepository.buttonSettings.first()
    }

    override suspend fun getBruteforceSettings(): BruteforceSettings {
        return bruteforceRepository.bruteforceSettings.first()
    }

    override suspend fun getButtonClicksData(buttonSelected: ButtonSelected): ButtonClicksData {
        return buttonSettingsRepository.getButtonClicksData(buttonSelected)
    }

    override fun getButtonSettingsFlow(): Flow<ButtonSettings> {
        return buttonSettingsRepository.buttonSettings
    }

    override fun getPermissionsFlow(): Flow<Permissions> {
        return permissionsRepository.permissions
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

    override suspend fun setClicksInRow(clicks: Int, buttonSelected: ButtonSelected) {
        buttonSettingsRepository.setClicksInRow(clicks, buttonSelected)
    }

    override suspend fun setLastTimestamp(timestamp: Long, buttonSelected: ButtonSelected) {
        buttonSettingsRepository.setLastTimestamp(timestamp, buttonSelected)
    }

    override suspend fun writeToLogs(text: String) {
        logsRepository.writeToLogs(text)
    }

    override suspend fun areLogsEnabled(): Boolean {
        return logsRepository.getLogsData().first().logsEnabled
    }
}