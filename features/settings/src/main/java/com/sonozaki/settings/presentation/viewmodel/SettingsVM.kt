package com.sonozaki.settings.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import com.sonozaki.entities.UsbSettings
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.bruteforce.GetBruteforceSettingsUseCase
import com.sonozaki.settings.domain.usecases.button.GetButtonSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetMultiuserUIUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSafeBootRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.usb.GetUsbSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserLimitUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserSwitchRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserSwitcherStatusUseCase
import com.sonozaki.settings.domain.usecases.bruteforce.SetBruteForceLimitUseCase
import com.sonozaki.settings.domain.usecases.bruteforce.SetBruteForceStatusUseCase
import com.sonozaki.settings.domain.usecases.button.SetClicksNumberUseCase
import com.sonozaki.settings.domain.usecases.button.SetLatencyUseCase
import com.sonozaki.settings.domain.usecases.settings.SetCleaItselfUseCase
import com.sonozaki.settings.domain.usecases.settings.SetClearDataUseCase
import com.sonozaki.settings.domain.usecases.settings.SetHideUseCase
import com.sonozaki.settings.domain.usecases.settings.SetLogdOnBootUseCase
import com.sonozaki.settings.domain.usecases.settings.SetLogdOnStartUseCase
import com.sonozaki.settings.domain.usecases.settings.SetMultiuserUIUseCase
import com.sonozaki.settings.domain.usecases.permissions.SetOwnerActiveUseCase
import com.sonozaki.settings.domain.usecases.settings.SetRemoveItselfUseCase
import com.sonozaki.settings.domain.usecases.permissions.SetRootActiveUseCase
import com.sonozaki.settings.domain.usecases.settings.SetRunOnDuressUseCase
import com.sonozaki.settings.domain.usecases.settings.SetSafeBootRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.SetScreenshotsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.SetThemeUseCase
import com.sonozaki.settings.domain.usecases.settings.SetTriggerOnButtonUseCase
import com.sonozaki.settings.domain.usecases.settings.SetTrimUseCase
import com.sonozaki.settings.domain.usecases.usb.SetUsbSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserLimitUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserSwitchRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserSwitcherUseCase
import com.sonozaki.settings.domain.usecases.settings.SetWipeUseCase
import com.sonozaki.settings.presentation.actions.SettingsAction
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val settingsActionChannel: Channel<SettingsAction>,
    private val setRemoveItselfUseCase: SetRemoveItselfUseCase,
    private val setTrimUseCase: SetTrimUseCase,
    private val setWipeUseCase: SetWipeUseCase,
    private val setOwnerActiveUseCase: SetOwnerActiveUseCase,
    private val setRootActiveUseCase: SetRootActiveUseCase,
    private val setUsbSettingsUseCase: SetUsbSettingsUseCase,
    private val setBruteForceStatusUseCase: SetBruteForceStatusUseCase,
    private val setBruteForceLimitUseCase: SetBruteForceLimitUseCase,
    private val superUserManager: SuperUserManager,
    private val setLogdOnBootUseCase: SetLogdOnBootUseCase,
    private val setLogdOnStartUseCase: SetLogdOnStartUseCase,
    private val setHideUseCase: SetHideUseCase,
    private val setMultiuserUIUseCase: SetMultiuserUIUseCase,
    private val getUserLimitUseCase: GetUserLimitUseCase,
    private val setUserLimitUseCase: SetUserLimitUseCase,
    private val setSafeBootRestrictionUseCase: SetSafeBootRestrictionUseCase,
    private val setRunOnDuressUseCase: SetRunOnDuressUseCase,
    private val setUserSwitcherUseCase: SetUserSwitcherUseCase,
    private val getSafeBootRestrictionUseCase: GetSafeBootRestrictionUseCase,
    private val setUserSwitchRestrictionUseCase: SetUserSwitchRestrictionUseCase,
    private val getUserSwitcherStatusUseCase: GetUserSwitcherStatusUseCase,
    private val getUserSwitchRestrictionUseCase: GetUserSwitchRestrictionUseCase,
    private val getMultiuserUIUseCase: GetMultiuserUIUseCase,
    private val setClearDataUseCase: SetClearDataUseCase,
    private val setClearItselfUseCase: SetCleaItselfUseCase,
    private val setLatencyUseCase: SetLatencyUseCase,
    private val setClicksNumberUseCase: SetClicksNumberUseCase,
    private val setTriggerOnButtonUseCase: SetTriggerOnButtonUseCase,
    private val setScreenshotsStatusUseCase: SetScreenshotsStatusUseCase,
    getPermissionsUseCase: GetPermissionsUseCase,
    getUSBSettingsUseCase: GetUsbSettingsUseCase,
    getBruteforceSettingsUseCase: GetBruteforceSettingsUseCase,
    getButtonSettingsUseCase: GetButtonSettingsUseCase,
) : ViewModel() {

    val settingsActionsFlow = settingsActionChannel.receiveAsFlow()

    val buttonsSettingsState = getButtonSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ButtonSettings()
    )

    val settingsState = getSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Settings()
    )

    val usbSettingState = getUSBSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UsbSettings.DO_NOTHING
    )

    val permissionsState = getPermissionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Permissions()
    )

    val bruteforceProtectionState = getBruteforceSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.sonozaki.entities.BruteforceSettings()
    )

    fun adminRightsIntent(): Intent {
        return superUserManager.askDeviceAdminRights()
    }

    fun setBruteForceLimit(limit: Int) {
        viewModelScope.launch {
            setBruteForceLimitUseCase(limit)
        }
    }

    fun setLatency(latency: Int) {
        viewModelScope.launch {
            setLatencyUseCase(latency)
        }
    }

    fun setClicksNumber(clicks: Int) {
        viewModelScope.launch {
            setClicksNumberUseCase(clicks)
        }
    }

    private fun showQuestionDialog(
        title: UIText.StringResource,
        message: UIText.StringResource,
        requestKey: String
    ) {
        viewModelScope.launch {
            showQuestionDialogSuspend(
                title, message, requestKey
            )
        }
    }

    private suspend fun showQuestionDialogSuspend(
        title: UIText.StringResource,
        message: UIText.StringResource,
        requestKey: String
    ) {
        settingsActionChannel.send(
            SettingsAction.ShowDialog(
                DialogActions.ShowQuestionDialog(
                    title, message, requestKey
                )
            )
        )
    }

    private fun showInfoDialog(
        title: UIText.StringResource,
        message: UIText.StringResource
    ) {
        viewModelScope.launch {
            showInfoDialogSuspend(
                title, message
            )
        }
    }



    private suspend fun showInputDigitDialogSuspend(title: UIText.StringResource,
                                                    message: UIText.StringResource,
                                                    hint: String,
                                                    range: IntRange,
                                                    requestKey: String)  {
        settingsActionChannel.send(
            SettingsAction.ShowDialog(
                DialogActions.ShowInputDigitDialog(
                    title = title,
                    message = message,
                    hint = hint,
                    range = range,
                    requestKey = requestKey
                )
            )
        )
    }

    private fun showInputDigitDialog(title: UIText.StringResource,
                                     message: UIText.StringResource,
                                     hint: String,
                                     range: IntRange,
                                     requestKey: String) {
        viewModelScope.launch {
            showInputDigitDialogSuspend(title,message,hint,range,requestKey)
        }
    }

    private suspend fun showInfoDialogSuspend(
        title: UIText.StringResource,
        message: UIText.StringResource,
    ) {
        settingsActionChannel.send(
            SettingsAction.ShowDialog(
                DialogActions.ShowInfoDialog(
                    title, message
                )
            )
        )
    }

    fun setUserLimit(limit: Int) {
        viewModelScope.launch {
            try {
                setUserLimitUseCase(limit)
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    title = UIText.StringResource(R.string.user_limit_not_changed),
                    message = e.messageForLogs
                )
            }
        }
    }

    private fun cantGetUserLimit(message: UIText.StringResource) {
        showInfoDialog(
            title = UIText.StringResource(R.string.cant_get_user_limit),
            message = message
        )
    }

    fun showChangeUserLimitDialog() {
        viewModelScope.launch {
            val hint = try {
                getUserLimitUseCase()
            } catch (e: SuperUserException) {
                cantGetUserLimit(e.messageForLogs)
                return@launch
            }
            if (hint == null) {
                cantGetUserLimit(UIText.StringResource(com.sonozaki.resources.R.string.number_not_found))
            }
            showInputDigitDialogSuspend(
                    title = UIText.StringResource(R.string.set_users_limit),
                    message = UIText.StringResource(R.string.set_users_limit_long),
                    hint = hint.toString(),
                    range = 1..1000,
                    requestKey = CHANGE_USER_LIMIT_DIALOG
            )
        }
    }

    fun setHide(status: Boolean) {
        viewModelScope.launch {
            setHideUseCase(status)
        }
    }

    fun showHideDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.hide_app),
            message = UIText.StringResource(R.string.hide_long),
            HIDE_DIALOG
        )
    }

    fun setClear(status: Boolean) {
        viewModelScope.launch {
            setClearItselfUseCase(status)
        }
    }

    fun showClearDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.clear_itself),
            message = UIText.StringResource(R.string.clear_itself_long),
            CLEAR_DIALOG
        )
    }

    fun setClearData(status: Boolean) {
        viewModelScope.launch {
            setClearDataUseCase(status)
        }
    }

    fun showClearDataDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.clear_data),
            message = UIText.StringResource(R.string.clear_data_long),
            CLEAR_DATA_DIALOG
        )
    }

    fun showRunOnDuressPasswordDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_on_password),
            message = UIText.StringResource(R.string.run_on_password_long),
            RUN_ON_PASSWORD_DIALOG
        )
    }

    fun setRunOnDuressPassword(status: Boolean) {
        viewModelScope.launch {
            setRunOnDuressUseCase(status)
        }
    }

    fun askDhizuku() {
        superUserManager.askDeviceOwnerRights(
            ::onDhizukuRightsApprove,
            ::onDhizukuRightsDeny,
            ::onDhizukuAbsent
        )
    }

    private fun onDhizukuAbsent() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.install_dhizuku),
            message = UIText.StringResource(R.string.install_dhizuku_long),
            requestKey = INSTALL_DIZUKU_DIALOG
        )
    }

    fun setSafeBoot(status: Boolean) {
        viewModelScope.launch {
            try {
                setSafeBootRestrictionUseCase(status)
                val title = if (status) {
                    R.string.safeboot_disabled
                } else {
                    R.string.safeboot_enabled
                }

                val message = if (status) {
                    R.string.safeboot_disabled_long
                } else {
                    R.string.safeboot_enabled_long
                }

                showInfoDialogSuspend(
                    title = UIText.StringResource(title),
                    message = UIText.StringResource(message)
                )
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    title = UIText.StringResource(R.string.safeboot_status_not_changed),
                    message = e.messageForLogs,
                )
            }
        }
    }

    fun setUserSwitcher(status: Boolean) {
        viewModelScope.launch {
            try {
                setUserSwitcherUseCase(status)
                if (status) {
                    showQuestionDialogSuspend(
                        title = UIText.StringResource(R.string.user_switcher_enabled),
                        message = UIText.StringResource(R.string.multiuser_ui_unlocked_long),
                        requestKey = OPEN_MULTIUSER_SETTINGS_DIALOG
                    )
                } else {
                    showInfoDialogSuspend(
                        title = UIText.StringResource(R.string.user_switcher_disabled),
                        message = UIText.StringResource(R.string.operation_successful)
                    )
                }
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    title = UIText.StringResource(R.string.user_switcher_status_change_failed),
                    message = e.messageForLogs,
                )
            }
        }
    }

    fun setUserSwitchRestriction(status: Boolean) {
        viewModelScope.launch {
            try {
                setUserSwitchRestrictionUseCase(status)
                if (status) {
                    showInfoDialogSuspend(
                        title = UIText.StringResource(R.string.user_switch_disabled),
                        message = UIText.StringResource(R.string.user_switch_disabled_long)
                    )
                } else {
                    showQuestionDialogSuspend(
                        title = UIText.StringResource(R.string.user_switch_enabled),
                        message = UIText.StringResource(R.string.multiuser_ui_unlocked_long),
                        requestKey = OPEN_MULTIUSER_SETTINGS_DIALOG
                    )
                }
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    title = UIText.StringResource(R.string.user_switch_status_change_failed),
                    message = e.messageForLogs,
                )
            }
        }
    }


    fun setMultiuserUI(status: Boolean) {
        viewModelScope.launch {
            try {
                setMultiuserUIUseCase(status)
                if (status) {
                    showQuestionDialogSuspend(
                        title = UIText.StringResource(R.string.multiuser_ui_unlocked),
                        message = UIText.StringResource(R.string.multiuser_ui_unlocked_long),
                        requestKey = OPEN_MULTIUSER_SETTINGS_DIALOG
                    )
                } else {
                    showInfoDialogSuspend(
                        title = UIText.StringResource(R.string.multiuser_ui_locked),
                        message = UIText.StringResource(R.string.operation_successful)
                    )
                }
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    title = UIText.StringResource(R.string.multiuser_ui_status_change_failed),
                    message = e.messageForLogs,
                )

            }
        }
    }

    fun changeSafeBootRestrictionSettingsDialog() {
        viewModelScope.launch {
            val enabled = try {
                getSafeBootRestrictionUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.enable_safe_boot
            } else {
                R.string.disable_safe_boot
            }
            val message = if (enabled) {
                R.string.enable_safe_boot_long
            } else {
                R.string.disable_safe_boot_long
            }

            val requestKey = if (enabled) {
                DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
            } else {
                ENABLE_SAFE_BOOT_RESTRICTION_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
        }
    }

    fun changeMultiuserUISettingsDialog() {
        viewModelScope.launch {
            val enabled = try {
                getMultiuserUIUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.disable_multiuser_ui
            } else {
                R.string.enable_multiuser_ui

            }
            val message = if (enabled) {
                R.string.disable_multiuser_ui_long
            } else {
                R.string.enable_multiuser_ui_long
            }

            val requestKey = if (enabled) {
                DISABLE_MULTIUSER_UI_DIALOG
            } else {
                ENABLE_MULTIUSER_UI_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
        }
    }

    fun changeUserSwitcherDialog() {
        viewModelScope.launch {
            val enabled = try {
                getUserSwitcherStatusUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.disable_user_switcher
            } else {
                R.string.enable_user_switcher

            }
            val message = if (enabled) {
                R.string.disable_user_switcher_ui_long
            } else {
                R.string.enable_user_switcher_ui_long
            }

            val requestKey = if (enabled) {
                DISABLE_USER_SWITCHER_DIALOG
            } else {
                ENABLE_USER_SWITCHER_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
        }
    }

    fun changeSwitchUserDialog() {
        viewModelScope.launch {
            val enabled = try {
                getUserSwitchRestrictionUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.enable_switch_user
            } else {
                R.string.disable_switch_user

            }
            val message = if (enabled) {
                R.string.enable_switch_user_long
            } else {
                R.string.disable_switch_user_long
            }

            val requestKey = if (enabled) {
                DISABLE_SWITCH_USER_RESTRICTION_DIALOG
            } else {
                ENABLE_SWITCH_USER_RESTRICTION_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
        }
    }

    private fun onDhizukuRightsApprove() {
        viewModelScope.launch {
            setOwnerActiveUseCase(true)
        }
    }

    private fun onDhizukuRightsDeny() {
        viewModelScope.launch {
            setOwnerActiveUseCase(false)
        }
    }

    fun askRoot() {
        viewModelScope.launch {
            val rootResult = superUserManager.askRootRights()
            setRootActiveUseCase(rootResult)
        }
    }

    fun setRunTRIM(status: Boolean) {
        viewModelScope.launch {
            setTrimUseCase(status)
        }
    }

    fun setWipe(status: Boolean) {
        viewModelScope.launch {
            setWipeUseCase(status)
        }
    }

    fun setRemoveItself(status: Boolean) {
        viewModelScope.launch {
            setRemoveItselfUseCase(status)
        }
    }

    fun setRunOnUsbConnection() {
        viewModelScope.launch {
            setUsbSettingsUseCase(com.sonozaki.entities.UsbSettings.RUN_ON_CONNECTION)
        }
    }

    fun setDoNothingOnUsbConnection() {
        viewModelScope.launch {
            setUsbSettingsUseCase(com.sonozaki.entities.UsbSettings.DO_NOTHING)
        }
    }

    fun setBruteforceProtection(status: Boolean) {
        viewModelScope.launch {
            setBruteForceStatusUseCase(status)
        }
    }


    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun showAccessibilityServiceDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.accessibility_service_title),
            message = UIText.StringResource(R.string.accessibility_service_long),
            MOVE_TO_ACCESSIBILITY_SERVICE
        )
    }

    fun showTRIMDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_trim),
            message = UIText.StringResource(R.string.trim_long),
            TRIM_DIALOG
        )
    }

    fun showWipeDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.wipe_data),
            message = UIText.StringResource(R.string.wipe_long),
            WIPE_DIALOG
        )
    }

    fun showSelfDestructionDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.remove_itself),
            message = UIText.StringResource(R.string.self_destruct_long),
            SELF_DESTRUCTION_DIALOG
        )
    }

    fun showRunOnUSBConnectionDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_on_connection),
            message = UIText.StringResource(R.string.run_on_usb_connection_long),
            RUN_ON_USB_DIALOG
        )
    }

    fun showBruteforceDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.bruteforce_defense),
            message = UIText.StringResource(R.string.bruteforce_defence_long),
            BRUTEFORCE_DIALOG
        )
    }

    fun editMaxPasswordAttemptsDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.allowed_attempts),
            message = UIText.StringResource(R.string.password_attempts_number),
            hint = bruteforceProtectionState.value.allowedAttempts.toString(),
            range = 1..1000,
            requestKey = MAX_PASSWORD_ATTEMPTS_DIALOG
        )
    }

    fun editButtonLatencyDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_button_latency),
            message = UIText.StringResource(R.string.button_latency_long),
            hint = buttonsSettingsState.value.latency.toString(),
            range = 100..100000,
            requestKey = EDIT_LATENCY_DIALOG
        )
    }

    fun editClicksNumberDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_clicks_number),
            message = UIText.StringResource(R.string.clicks_number_long),
            hint = buttonsSettingsState.value.allowedClicks.toString(),
            range = 3..20,
            requestKey = EDIT_CLICK_NUMBER_DIALOG
        )
    }

    fun setTriggerOnButton(status: Boolean) {
        viewModelScope.launch {
            setTriggerOnButtonUseCase(status)
        }
    }

    fun setRebootOnUSB() {
        viewModelScope.launch {
            setUsbSettingsUseCase(com.sonozaki.entities.UsbSettings.REBOOT_ON_CONNECTION)
        }
    }

    fun showSetTriggerOnButtonDialog() {
        showQuestionDialog(title = UIText.StringResource(R.string.destroy_on_button_title),
            message = UIText.StringResource(R.string.destroy_on_button_long),
            requestKey = TRIGGER_ON_BUTTON_DIALOG
        )
    }

    fun showRebootOnUSBDialog() {
        if (permissionsState.value.isRoot || permissionsState.value.isOwner)
            showQuestionDialog(title = UIText.StringResource(R.string.reboot_on_usb),
                message = UIText.StringResource(R.string.reboot_on_usb_long),
                requestKey = REBOOT_ON_USB_DIALOG
            )
        else
            showInfoDialog(UIText.StringResource(com.sonozaki.resources.R.string.no_superuser_rights), UIText.StringResource(R.string.provide_root_or_dhizuku))
    }

    fun showFaq() {
        viewModelScope.launch {
            settingsActionChannel.send(
                SettingsAction.ShowFaq
            )
        }
    }

    fun showRootWarningDialog() {
        showQuestionDialog(title = UIText.StringResource(R.string.grant_root_rights),
            message = UIText.StringResource(R.string.warning_root_rights),
            ROOT_WARNING_DIALOG
        )
    }

    fun disableAdmin() {
        viewModelScope.launch {
            try {
                superUserManager.removeAdminRights()
            } catch (e: SuperUserException) {
                showInfoDialog(
                    title = UIText.StringResource(R.string.disabling_admin_failed),
                    message = e.messageForLogs
                )
            }
        }
    }

    fun showRootDisableDialog() {
        viewModelScope.launch {
            showInfoDialogSuspend(
                message = UIText.StringResource(R.string.disable_root_long),
                title = UIText.StringResource(R.string.disable_root)
            )
            setRootActiveUseCase(false)
        }
    }

    fun showAllowScreenShotsDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.allow_screenshots),
            message = UIText.StringResource(R.string.allow_screenshots_long),
            ALLOW_SCREENSHOTS_DIALOG
        )
    }

    fun setScreenShotsStatus(status: Boolean) {
        viewModelScope.launch {
            setScreenshotsStatusUseCase(status)
        }
    }

    fun setLogdOnStartStatus(status: Boolean) {
        viewModelScope.launch {
            setLogdOnStartUseCase(status)
        }
    }

    fun setLogdOnBootStatus(status: Boolean) {
        viewModelScope.launch {
            setLogdOnBootUseCase(status)
        }
    }

    fun showLogdOnBootDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.logd_on_boot),
            message = UIText.StringResource(R.string.logd_on_boot_long),
            LOGD_ON_BOOT_DIALOG
        )
    }

    fun showLogdOnStartDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.logd_on_start),
            message = UIText.StringResource(R.string.logd_on_start_long),
            LOGD_ON_START_DIALOG
        )
    }

    fun destroyDataDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.destroy_data),
            message = UIText.StringResource(R.string.destroy_data_long),
            DESTROY_DATA_DIALOG
        )
    }


    companion object {
        const val MOVE_TO_ACCESSIBILITY_SERVICE = "move_to_accessibility_service"
        const val MOVE_TO_ADMIN_SETTINGS = "move_to_admin_settings"
        const val TRIM_DIALOG = "trim_dialog"
        const val WIPE_DIALOG = "wipe_dialog"
        const val SELF_DESTRUCTION_DIALOG = "selfdestruct_dialog"
        const val RUN_ON_USB_DIALOG = "usb_dialog"
        const val BRUTEFORCE_DIALOG = "bruteforce_dialog"
        const val INSTALL_DIZUKU_DIALOG = "install_dizuku"
        const val LOGD_ON_BOOT_DIALOG = "logd_on_boot"
        const val LOGD_ON_START_DIALOG = "logd_on_start"
        const val HIDE_DIALOG = "hide_dialog"
        const val CLEAR_DIALOG = "clear_dialog"
        const val CLEAR_DATA_DIALOG = "clear_data_dialog"
        const val CHANGE_USER_LIMIT_DIALOG = "change_user_limit"
        const val MAX_PASSWORD_ATTEMPTS_DIALOG = "max_password_attempts"
        const val OPEN_MULTIUSER_SETTINGS_DIALOG = "open_multiuser_settings_dialog"
        const val RUN_ON_PASSWORD_DIALOG = "run_on_password_dialog"
        const val DISABLE_SAFE_BOOT_RESTRICTION_DIALOG = "disable_safe_boot_restriction_dialog"
        const val ENABLE_SAFE_BOOT_RESTRICTION_DIALOG = "enable_safe_boot_restriction_dialog"
        const val DISABLE_MULTIUSER_UI_DIALOG = "disable_multiuser_ui_dialog"
        const val ENABLE_MULTIUSER_UI_DIALOG = "enable_multiuser_ui_dialog"
        const val DISABLE_USER_SWITCHER_DIALOG = "disable_user_switcher_dialog"
        const val ENABLE_USER_SWITCHER_DIALOG = "enable_user_switcher_dialog"
        const val DISABLE_SWITCH_USER_RESTRICTION_DIALOG = "disable_switch_user_restriction_dialog"
        const val ENABLE_SWITCH_USER_RESTRICTION_DIALOG = "enable_switch_user_restriction_dialog"
        const val EDIT_LATENCY_DIALOG = "edit_latency_dialog"
        const val EDIT_CLICK_NUMBER_DIALOG = "edit_click_number_dialog"
        const val TRIGGER_ON_BUTTON_DIALOG = "trigger_on_button_dialog"
        const val REBOOT_ON_USB_DIALOG = "reboot_on_usb_dialog"
        const val ROOT_WARNING_DIALOG = "root_warning_dialog"
        const val DESTROY_DATA_DIALOG = "destroy_data_dialog"
        const val ALLOW_SCREENSHOTS_DIALOG = "allow_screenshots_dialog"
    }

}
