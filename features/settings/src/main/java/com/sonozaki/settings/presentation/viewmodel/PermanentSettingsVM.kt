package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.deviceProtection.GetDeviceProtectionSettingsUseCase
import com.sonozaki.settings.domain.usecases.deviceProtection.SetRebootDelayUseCase
import com.sonozaki.settings.domain.usecases.deviceProtection.SetRebootOnLockStatusUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetDeveloperSettingsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.GetLogsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSafeBootRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetUserLimitUseCase
import com.sonozaki.settings.domain.usecases.settings.SetDeveloperSettingsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.SetLogdOnBootUseCase
import com.sonozaki.settings.domain.usecases.settings.SetLogdOnStartUseCase
import com.sonozaki.settings.domain.usecases.settings.SetLogsStatusUseCase
import com.sonozaki.settings.domain.usecases.settings.SetSafeBootRestrictionUseCase
import com.sonozaki.settings.domain.usecases.settings.SetUserLimitUseCase
import com.sonozaki.superuser.superuser.SuperUserException
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermanentSettingsVM @Inject constructor(
    private val getLogsStatusUseCase: GetLogsStatusUseCase,
    private val getDeveloperSettingsStatusUseCase: GetDeveloperSettingsStatusUseCase,
    private val getSafeBootRestrictionUseCase: GetSafeBootRestrictionUseCase,
    private val getUserLimitUseCase: GetUserLimitUseCase,
    private val setRebootDelayUseCase: SetRebootDelayUseCase,
    private val setRebootOnLockStatusUseCase: SetRebootOnLockStatusUseCase,
    private val setLogdOnStartUseCase: SetLogdOnStartUseCase,
    private val setLogdOnBootUseCase: SetLogdOnBootUseCase,
    private val setLogsStatusUseCase: SetLogsStatusUseCase,
    private val setDeveloperSettingsStatusUseCase: SetDeveloperSettingsStatusUseCase,
    private val setSafeBootRestrictionUseCase: SetSafeBootRestrictionUseCase,
    private val setUserLimitUseCase: SetUserLimitUseCase,
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

    fun moveDeviceToBFUAutomaticallyDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.auto_move_to_bfu),
            message = UIText.StringResource(R.string.move_to_bfu_long),
            requestKey = MOVE_TO_BFU_DIALOG
        )
    }


    fun changeLogsStatusDialog() {
        viewModelScope.launch {
            val enabled = try {
                getLogsStatusUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.disable_logs
            } else {
                R.string.enable_logs

            }
            val message = if (enabled) {
                R.string.disable_logs_long
            } else {
                R.string.enable_logs_long
            }

            val requestKey = if (enabled) {
                DISABLE_LOGS_DIALOG
            } else {
                ENABLE_LOGS_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
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

    fun changeDeveloperSettingsStatusDialog() {
        viewModelScope.launch {
            val enabled = try {
                getDeveloperSettingsStatusUseCase()
            } catch (e: SuperUserException) {
                showInfoDialogSuspend(
                    UIText.StringResource(R.string.error_while_loading_data),
                    e.messageForLogs
                )
                return@launch
            }
            val title = if (enabled) {
                R.string.disable_developer_settings
            } else {
                R.string.enable_developer_settings

            }
            val message = if (enabled) {
                R.string.disable_developer_settings_long
            } else {
                R.string.enable_developer_settings_long
            }

            val requestKey = if (enabled) {
                DISABLE_DEV_SETTINGS_DIALOG
            } else {
                ENABLE_DEV_SETTINGS_DIALOG
            }
            showQuestionDialogSuspend(
                title = UIText.StringResource(title),
                message = UIText.StringResource(message),
                requestKey = requestKey
            )
        }
    }

    fun changeMoveToBFUDelay(delay: Int) {
        viewModelScope.launch {
            setRebootDelayUseCase(delay)
        }
    }

    fun setMoveToBFU(status: Boolean) {
        viewModelScope.launch {
            setRebootOnLockStatusUseCase(status)
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

    fun showMovingToBFUDelayDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_moving_to_bfu_delay),
            message = UIText.StringResource(R.string.change_moving_to_bfu_delay_long),
            hint = deviceProtectionSettingsState.value.rebootDelay.toString(),
            range = 1..1000000,
            requestKey = EDIT_BFU_DELAY_DIALOG
        )
    }

    fun setLogsStatus(enable: Boolean) {
        viewModelScope.launch {
            setLogsStatusUseCase(enable)
        }
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

    fun setDevOptionsStatus(status: Boolean) {
        viewModelScope.launch {
            setDeveloperSettingsStatusUseCase(status)
        }
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

    companion object {
        const val DISABLE_LOGS_DIALOG = "disable_logs_dialog"
        const val ENABLE_LOGS_DIALOG = "enable_logs_dialog"
        const val DISABLE_DEV_SETTINGS_DIALOG = "disable_dev_settings_dialog"
        const val ENABLE_DEV_SETTINGS_DIALOG = "enable_dev_settings_dialog"
        const val DISABLE_SAFE_BOOT_RESTRICTION_DIALOG = "disable_safe_boot_restriction_dialog"
        const val ENABLE_SAFE_BOOT_RESTRICTION_DIALOG = "enable_safe_boot_restriction_dialog"
        const val EDIT_BFU_DELAY_DIALOG = "edit_bfu_delay_dialog"
        const val CHANGE_USER_LIMIT_DIALOG = "change_user_limit"
        const val MOVE_TO_BFU_DIALOG = "move_device_to_bfu_dialog"
        const val LOGD_ON_BOOT_DIALOG = "logd_on_boot"
        const val LOGD_ON_START_DIALOG = "logd_on_start"
    }
}