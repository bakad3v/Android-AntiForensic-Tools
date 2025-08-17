package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.entities.AppState
import com.bakasoft.setupwizard.domain.entities.DataSelected
import com.bakasoft.setupwizard.domain.entities.SettingsElementState
import com.bakasoft.setupwizard.domain.entities.SetupWizardState
import com.bakasoft.setupwizard.domain.entities.WizardElement
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.EnumMap
import javax.inject.Inject

class GetWizardStateUseCase @Inject constructor(
    private val repository: SetupWizardRepository,
    private val superUserManager: SuperUserManager
) {
    operator fun invoke(): Flow<SetupWizardState.Data> {
        return combine(
            repository.settings,
            repository.permissions,
            repository.usbSettings,
            repository.buttonSettings,
            repository.bruteforceSettings,
            repository.deviceProtectionSettings,
            repository.appLatestData,
            repository.filesSelected,
            repository.profilesSelected,
            repository.rootCommandNotEmpty,
        ) { settings: Settings, permissions: Permissions, usbSettings: UsbSettings,
            buttonSettings: ButtonSettings, bruteforceSettings: BruteforceSettings,
            deviceProtectionSettings: DeviceProtectionSettings, appLatestData: AppLatestVersion?,
            filesSelected: Boolean, profilesSelected: Boolean, rootCommandNotEmpty: Boolean ->
            val testOnlyNeeded = permissions.isAdmin && permissions.isRoot && settings.removeItself
            val appVersionState = getAppVersionState(appLatestData, testOnlyNeeded)

            val accessibilityServiceState = getAccessibilityServiceState(settings)

            val usbTriggerState = getUsbTriggerState(usbSettings)

            val passwordState = getPasswordState(settings)

            val buttonClicksState = getButtonClicksState(buttonSettings)

            val bruteforceState = getBruteforceState(bruteforceSettings)

            val selectedData =
                getSelectedData(profilesSelected, settings, filesSelected, rootCommandNotEmpty)

            val selectedDataState = when (selectedData) {
                DataSelected.PROFILES -> SettingsElementState.OK
                DataSelected.WIPE, DataSelected.FILES, DataSelected.ROOT -> SettingsElementState.RECOMMENDED
                DataSelected.NONE -> SettingsElementState.REQUIRED
            }

            val activeDestructionStateCheck = when (selectedData) {
                DataSelected.PROFILES -> settings.deleteProfiles
                DataSelected.FILES -> settings.deleteFiles
                DataSelected.ROOT -> settings.runRoot
                DataSelected.WIPE -> true
                DataSelected.NONE -> false
            }

            val activeDestructionState = if (activeDestructionStateCheck) {
                SettingsElementState.OK
            } else {
                SettingsElementState.REQUIRED
            }

            val enableSelfDestructionState = getEnableSelfDestructionState(selectedData, settings)

            val appHidingState = getAppHidingState(selectedData, permissions, settings)

            val disableSafeBootState = getDisableSafeBootState()

            val disableLogsState = getDisableLogsState(selectedData)

            val disableMultiuserUIState = getDisableMultiuserUIState(selectedData, profilesSelected)

            val activateTrimState = getActivateTrimState(selectedData, settings)

            val protectionFixAvailable = getProtectionAvailable(permissions, selectedData)
            val superUserPermissionsState =
                getSuperUserPermissionsState(permissions, protectionFixAvailable)

            val dataMap = EnumMap<WizardElement, SettingsElementState>(WizardElement::class.java).apply {
                put(WizardElement.CORRECT_APP_VERSION, appVersionState)
                put(WizardElement.ACCESSIBILITY_SERVICE, accessibilityServiceState)
                put(WizardElement.SUPERUSER_PERMISSIONS, superUserPermissionsState)
                put(WizardElement.TRIGGER_ON_USB, usbTriggerState)
                put(WizardElement.TRIGGER_ON_PASSWORD, passwordState)
                put(WizardElement.TRIGGER_ON_CLICKS, buttonClicksState)
                put(WizardElement.TRIGGER_ON_BRUTEFORCE, bruteforceState)
                put(WizardElement.SELECT_DATA, selectedDataState)
                put(WizardElement.ACTIVATE_DESTRUCTION, activeDestructionState)
                put(WizardElement.ENABLE_SELF_DESTRUCTION, enableSelfDestructionState)
                put(WizardElement.ENABLE_HIDING, appHidingState)
                put(WizardElement.DISABLE_SAFE_BOOT, disableSafeBootState)
                put(WizardElement.DISABLE_LOGS, disableLogsState)
                put(WizardElement.SETUP_MULTIUSER, disableMultiuserUIState)
                put(WizardElement.ACTIVATE_TRIM, activateTrimState)
            }

            val appState = if (dataMap.containsValue(SettingsElementState.REQUIRED)) {
                AppState.BAD
            } else if (dataMap.containsValue(SettingsElementState.RECOMMENDED)) {
                AppState.WITH_WARNINGS
            } else {
                AppState.NICE
            }

            SetupWizardState.Data(
                dataMap = dataMap,
                state = appState,
                dataSelected = selectedData,
                protectionFixActive = protectionFixAvailable,
                triggersFixActive = settings.serviceWorking)
        }
    }

    private fun getActivateTrimState(
        selectedData: DataSelected,
        settings: Settings
    ): SettingsElementState = when (selectedData) {
        DataSelected.WIPE, DataSelected.NONE -> SettingsElementState.NOT_NEEDED
        DataSelected.ROOT -> SettingsElementState.UNKNOW
        DataSelected.FILES, DataSelected.PROFILES ->
            if (settings.trim) {
                SettingsElementState.OK
            } else {
                SettingsElementState.REQUIRED
            }
    }

    private fun getDisableMultiuserUIState(
        selectedData: DataSelected,
        profilesSelected: Boolean
    ): SettingsElementState = when (selectedData) {
        DataSelected.PROFILES -> if (profilesSelected) {
            SettingsElementState.OK
        } else {
            SettingsElementState.REQUIRED
        }

        else -> SettingsElementState.NOT_NEEDED
    }

    private suspend fun getDisableLogsState(selectedData: DataSelected): SettingsElementState =
        when (selectedData) {
            DataSelected.WIPE, DataSelected.NONE -> SettingsElementState.NOT_NEEDED
            DataSelected.ROOT -> SettingsElementState.UNKNOW
            DataSelected.FILES, DataSelected.PROFILES -> try {
                if (superUserManager.getSuperUser()
                        .getLogsStatus()
                ) {
                    SettingsElementState.REQUIRED
                } else {
                    SettingsElementState.OK
                }
            } catch (e: SuperUserException) {
                SettingsElementState.RECOMMENDED
            }
        }

    private fun getProtectionAvailable(permissions: Permissions, selected: DataSelected): Boolean {
        return when(selected) {
            DataSelected.PROFILES, DataSelected.FILES -> permissions.isRoot || (permissions.isShizuku && permissions.isOwner)
            DataSelected.WIPE, DataSelected.ROOT -> permissions.isRoot || permissions.isShizuku || permissions.isOwner
            DataSelected.NONE -> false
        }
    }

    private suspend fun getDisableSafeBootState(): SettingsElementState =
        try {
            if (superUserManager.getSuperUser().getSafeBootStatus()) {
                SettingsElementState.OK
            } else {
                SettingsElementState.REQUIRED
            }
        } catch (e: Exception) {
            SettingsElementState.RECOMMENDED
        }

    private fun getAppHidingState(
        selectedData: DataSelected,
        permissions: Permissions,
        settings: Settings
    ): SettingsElementState = when (selectedData) {
        DataSelected.WIPE, DataSelected.NONE -> SettingsElementState.NOT_NEEDED
        DataSelected.ROOT -> SettingsElementState.UNKNOW
        DataSelected.FILES, DataSelected.PROFILES -> if (!permissions.isRoot) {
            if (settings.hideItself) {
                SettingsElementState.OK
            } else {
                SettingsElementState.REQUIRED
            }
        } else {
            SettingsElementState.NOT_NEEDED
        }
    }

    private fun getEnableSelfDestructionState(
        selectedData: DataSelected,
        settings: Settings
    ): SettingsElementState = when (selectedData) {
        DataSelected.WIPE, DataSelected.NONE -> SettingsElementState.NOT_NEEDED
        DataSelected.ROOT -> SettingsElementState.UNKNOW
        DataSelected.FILES, DataSelected.PROFILES -> if (settings.removeItself) {
            SettingsElementState.OK
        } else {
            SettingsElementState.REQUIRED
        }
    }

    private fun getSelectedData(
        profilesSelected: Boolean,
        settings: Settings,
        filesSelected: Boolean,
        rootCommandNotEmpty: Boolean
    ): DataSelected = if (profilesSelected) {
        DataSelected.PROFILES
    } else if (settings.wipe) {
        DataSelected.WIPE
    } else if (filesSelected) {
        DataSelected.FILES
    } else if (rootCommandNotEmpty) {
        DataSelected.ROOT
    } else {
        DataSelected.NONE
    }

    private fun getBruteforceState(bruteforceSettings: BruteforceSettings): SettingsElementState =
        when (bruteforceSettings.detectingMethod) {
            BruteforceDetectingMethod.ADMIN, BruteforceDetectingMethod.ACCESSIBILITY_SERVICE ->
                SettingsElementState.OK

            BruteforceDetectingMethod.NONE -> SettingsElementState.REQUIRED
        }

    private fun getButtonClicksState(buttonSettings: ButtonSettings): SettingsElementState =
        when (buttonSettings.triggerOnButton) {
            PowerButtonTriggerOptions.DEPRECATED_WAY -> SettingsElementState.RECOMMENDED
            PowerButtonTriggerOptions.SUPERUSER_WAY -> SettingsElementState.OK
            PowerButtonTriggerOptions.IGNORE -> when (buttonSettings.triggerOnVolumeButton) {
                VolumeButtonTriggerOptions.ON_VOLUME_UP, VolumeButtonTriggerOptions.ON_VOLUME_DOWN ->
                    SettingsElementState.OK

                VolumeButtonTriggerOptions.IGNORE -> SettingsElementState.REQUIRED
            }
        }

    private fun getPasswordState(settings: Settings): SettingsElementState =
        if (settings.runOnDuressPassword) {
            SettingsElementState.OK
        } else {
            SettingsElementState.REQUIRED
        }

    private fun getUsbTriggerState(usbSettings: UsbSettings): SettingsElementState = when (usbSettings) {
        UsbSettings.RUN_ON_CONNECTION -> SettingsElementState.OK
        UsbSettings.REBOOT_ON_CONNECTION -> SettingsElementState.RECOMMENDED
        UsbSettings.DO_NOTHING -> SettingsElementState.REQUIRED
    }

    private fun getSuperUserPermissionsState(permissions: Permissions, protectionFixAvailable: Boolean): SettingsElementState =
        if (permissions.isRoot || permissions.isAdmin || permissions.isShizuku || permissions.isOwner) {
            if (protectionFixAvailable) {
                SettingsElementState.OK
            } else {
                SettingsElementState.RECOMMENDED
            }
        } else {
            SettingsElementState.REQUIRED
        }

    private fun getAccessibilityServiceState(settings: Settings): SettingsElementState = if (settings.serviceWorking) {
        SettingsElementState.OK
    } else {
        SettingsElementState.REQUIRED
    }

    private fun getAppVersionState(
        appLatestData: AppLatestVersion?,
        testOnlyNeeded: Boolean
    ): SettingsElementState = if (appLatestData == null) {
        SettingsElementState.UNKNOW
    } else if (testOnlyNeeded && !appLatestData.isTestOnly) {
        SettingsElementState.REQUIRED
    } else if (appLatestData.newVersion) {
        SettingsElementState.RECOMMENDED
    } else {
        SettingsElementState.OK
    }

    inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        flow9: Flow<T9>,
        flow10: Flow<T10>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
    ): Flow<R> {
        return combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6,
            flow7,
            flow8,
            flow9,
            flow10) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
                args[7] as T8,
                args[8] as T9,
                args[9] as T10
            )
        }
    }
}