package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.deviceProtection.GetDeviceProtectionSettingsUseCase
import com.sonozaki.settings.domain.usecases.deviceProtection.SetMultiuserUIProtectionUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetMultiuserUIUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserSwitchRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserSwitcherStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.SetMultiuserUIUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserSwitchRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserSwitcherUseCase
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiuserSettingsVM @Inject constructor(
    private val setMultiuserUIProtectionUseCase: SetMultiuserUIProtectionUseCase,
    private val setMultiuserUIUseCase: SetMultiuserUIUseCase,
    private val setUserSwitcherUseCase: SetUserSwitcherUseCase,
    private val setUserSwitchRestrictionUseCase: SetUserSwitchRestrictionUseCase,
    private val getMultiuserUIUseCase: GetMultiuserUIUseCase,
    private val getUserSwitchRestrictionUseCase: GetUserSwitchRestrictionUseCase,
    private val getUserSwitcherStatusUseCase: GetUserSwitcherStatusUseCase,
    getDeviceProtectionSettingsUseCase: GetDeviceProtectionSettingsUseCase,
    settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    val deviceProtectionSettingsState = getDeviceProtectionSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        DeviceProtectionSettings()
    )

    fun setMultiUserUIProtection(multiuserUIProtection: MultiuserUIProtection) {
        viewModelScope.launch {
            setMultiuserUIProtectionUseCase(multiuserUIProtection)
        }
    }

    fun disableMultiuserUIOnBootDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.disable_multiuser_ui_automatically_dialog),
            message = UIText.StringResource(R.string.disable_multiuser_ui_automatically_on_boot_long),
            requestKey = DISABLE_MULTIUSER_UI_ON_BOOT
        )
    }

    fun disableMultiuserUIonLockDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.disable_multiuser_ui_automatically_dialog),
            message = UIText.StringResource(R.string.disable_multiuser_ui_automatically_on_lock_long),
            requestKey = DISABLE_MULTIUSER_UI_ON_LOCK
        )
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

    companion object {
        const val DISABLE_MULTIUSER_UI_ON_BOOT = "disable_multiuser_ui_on_boot"
        const val DISABLE_MULTIUSER_UI_ON_LOCK = "disable_multiuser_ui_on_lock"
        const val OPEN_MULTIUSER_SETTINGS_DIALOG = "open_multiuser_settings_dialog"
        const val DISABLE_MULTIUSER_UI_DIALOG = "disable_multiuser_ui_dialog"
        const val ENABLE_MULTIUSER_UI_DIALOG = "enable_multiuser_ui_dialog"
        const val DISABLE_USER_SWITCHER_DIALOG = "disable_user_switcher_dialog"
        const val ENABLE_USER_SWITCHER_DIALOG = "enable_user_switcher_dialog"
        const val DISABLE_SWITCH_USER_RESTRICTION_DIALOG = "disable_switch_user_restriction_dialog"
        const val ENABLE_SWITCH_USER_RESTRICTION_DIALOG = "enable_switch_user_restriction_dialog"
    }
}