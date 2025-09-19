package com.android.aftools.adapters

import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.network.RequestResult
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import com.sonozaki.data.files.repository.FilesRepository
import com.sonozaki.data.profiles.repository.ProfilesRepository
import com.sonozaki.data.settings.repositories.BruteforceRepository
import com.sonozaki.data.settings.repositories.ButtonSettingsRepository
import com.sonozaki.data.settings.repositories.DeviceProtectionSettingsRepository
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.data.settings.repositories.UsbSettingsRepository
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.root.repository.RootRepository
import com.sonozaki.superuser.superuser.SuperUserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SetupWizardRepositoryAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val filesRepository: FilesRepository,
    private val profilesRepository: ProfilesRepository,
    private val usbSettingsRepository: UsbSettingsRepository,
    private val buttonSettingsRepository: ButtonSettingsRepository,
    private val bruteforceRepository: BruteforceRepository,
    private val permissionsRepository: PermissionsRepository,
    private val deviceProtectionSettingsRepository: DeviceProtectionSettingsRepository,
    private val appUpdateRepository: AppUpdateRepository,
    private val superUserManager: SuperUserManager,
    private val rootRepository: RootRepository,
): SetupWizardRepository {
    override val permissions: Flow<Permissions>
        get() = permissionsRepository.permissions
    override val settings: Flow<Settings>
        get() = settingsRepository.settings
    override val filesSelected: Flow<Boolean>
        get() = filesRepository.getFiles().map { it.list.isNotEmpty() }
    override val profilesSelected: Flow<Boolean>
        get() = profilesRepository.getProfiles().map { it?.any { it.toDelete } == true }
    override val usbSettings: Flow<UsbSettings>
        get() = usbSettingsRepository.usbSettings
    override val buttonSettings: Flow<ButtonSettings>
        get() = buttonSettingsRepository.buttonSettings
    override val bruteforceSettings: Flow<BruteforceSettings>
        get() = bruteforceRepository.bruteforceSettings
    override val deviceProtectionSettings: Flow<DeviceProtectionSettings>
        get() = deviceProtectionSettingsRepository.deviceProtectionSettings
    override val appLatestData: Flow<AppLatestVersion?>
        get() = appUpdateRepository.appUpdateDataFlow.map {
            when(it) {
                is RequestResult.Error -> null
                is RequestResult.Data<AppLatestVersion> -> it.data
            }
        }

    override val rootCommandNotEmpty = rootRepository.getRootCommand().map { it.isNotBlank() }

    override suspend fun checkUpdates() {
        appUpdateRepository.checkUpdates()
    }

    override suspend fun refreshProfiles() {
        profilesRepository.refreshDeviceProfiles()
    }

    override suspend fun setTriggerOnUsb() {
       usbSettingsRepository.setUsbSettings(UsbSettings.RUN_ON_CONNECTION)
    }

    override suspend fun setTriggerOnDuressPassword() {
        settingsRepository.setRunOnDuressPassword(true)
    }

    override suspend fun setTriggerOnVolumeButtonClicks() {
        buttonSettingsRepository.setTriggerOnVolumeButtonStatus(VolumeButtonTriggerOptions.ON_VOLUME_UP)
    }

    override suspend fun setTriggerOnPowerButtonClicks() {
        buttonSettingsRepository.setTriggerOnButtonStatus(PowerButtonTriggerOptions.SUPERUSER_WAY)
    }

    override suspend fun setTriggerOnBruteforce() {
        bruteforceRepository.setBruteforceStatus(BruteforceDetectingMethod.ACCESSIBILITY_SERVICE)
    }

    override suspend fun setSelfDestruction() {
        settingsRepository.setRemoveItself(true)
    }

    override suspend fun setAppHiding() {
        settingsRepository.setHide(true)
    }

    override suspend fun disableLogs() {
        superUserManager.getSuperUser().changeLogsStatus(false)

    }

    override suspend fun disableSafeBoot() {
        superUserManager.getSuperUser().setSafeBootStatus(true)
    }

    override suspend fun setupMultiuser() {
        deviceProtectionSettingsRepository.changeMultiuserUIProtection(MultiuserUIProtection.ON_SCREEN_OFF)
    }

    override suspend fun activateTrim() {
        settingsRepository.setTRIM(true)
    }

}