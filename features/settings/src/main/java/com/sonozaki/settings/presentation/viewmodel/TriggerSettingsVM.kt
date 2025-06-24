package com.sonozaki.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.settings.R
import com.sonozaki.settings.domain.usecases.bruteforce.GetBruteforceSettingsUseCase
import com.sonozaki.settings.domain.usecases.bruteforce.SetBruteForceLimitUseCase
import com.sonozaki.settings.domain.usecases.bruteforce.SetBruteForceStatusUseCase
import com.sonozaki.settings.domain.usecases.button.GetButtonSettingsUseCase
import com.sonozaki.settings.domain.usecases.button.SetClicksNumberUseCase
import com.sonozaki.settings.domain.usecases.button.SetClicksNumberVolumeUseCase
import com.sonozaki.settings.domain.usecases.button.SetLatencyUseCase
import com.sonozaki.settings.domain.usecases.button.SetLatencyVolumeUseCase
import com.sonozaki.settings.domain.usecases.button.SetRootLatencyUseCase
import com.sonozaki.settings.domain.usecases.button.SetTriggerOnPowerButtonUseCase
import com.sonozaki.settings.domain.usecases.button.SetTriggerOnVolumeButtonUseCase
import com.sonozaki.settings.domain.usecases.permissions.GetPermissionsUseCase
import com.sonozaki.settings.domain.usecases.settings.GetSettingsUseCase
import com.sonozaki.settings.domain.usecases.settings.SetRunOnDuressUseCase
import com.sonozaki.settings.domain.usecases.usb.GetUsbSettingsUseCase
import com.sonozaki.settings.domain.usecases.usb.SetUsbSettingsUseCase
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TriggerSettingsVM @Inject constructor(
    private val setTriggerOnVolumeButtonUseCase: SetTriggerOnVolumeButtonUseCase,
    private val setTriggerOnPowerButtonUseCase: SetTriggerOnPowerButtonUseCase,
    private val setUsbSettingsUseCase: SetUsbSettingsUseCase,
    private val setBruteForceStatusUseCase: SetBruteForceStatusUseCase,
    private val setRunOnDuressUseCase: SetRunOnDuressUseCase,
    private val setBruteForceLimitUseCase: SetBruteForceLimitUseCase,
    private val setLatencyUseCase: SetLatencyUseCase,
    private val setLatencyVolumeUseCase: SetLatencyVolumeUseCase,
    private val setRootLatencyUseCase: SetRootLatencyUseCase,
    private val setClicksNumberUseCase: SetClicksNumberUseCase,
    private val setClicksNumberVolumeUseCase: SetClicksNumberVolumeUseCase,
    getUSBSettingsUseCase: GetUsbSettingsUseCase,
    getButtonSettingsUseCase: GetButtonSettingsUseCase,
    getBruteforceSettingsUseCase: GetBruteforceSettingsUseCase,
    settingsActionChannel: Channel<DialogActions>,
    getSettingsUseCase: GetSettingsUseCase,
    getPermissionsUseCase: GetPermissionsUseCase
): AbstractSettingsVM(settingsActionChannel, getSettingsUseCase, getPermissionsUseCase) {

    val buttonsSettingsState = getButtonSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        ButtonSettings()
    )

    val bruteforceProtectionState = getBruteforceSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        BruteforceSettings()
    )

    val usbSettingState = getUSBSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        UsbSettings.DO_NOTHING
    )

    fun setTriggerOnVolumeButton(state: VolumeButtonTriggerOptions) {
        viewModelScope.launch {
            setTriggerOnVolumeButtonUseCase(state)
        }
    }

    fun setClicksNumber(clicks: Int) {
        viewModelScope.launch {
            setClicksNumberUseCase(clicks)
        }
    }

    fun setClicksNumberVolume(clicks: Int) {
        viewModelScope.launch {
            setClicksNumberVolumeUseCase(clicks)
        }
    }

    fun setRootLatency(latency: Int) {
        viewModelScope.launch {
            setRootLatencyUseCase(latency)
        }
    }

    fun setVolumeLatency(latency: Int) {
        viewModelScope.launch {
            setLatencyVolumeUseCase(latency)
        }
    }

    fun setRebootOnUSB() {
        viewModelScope.launch {
            setUsbSettingsUseCase(UsbSettings.REBOOT_ON_CONNECTION)
        }
    }

    fun setTriggerOnButton(status: PowerButtonTriggerOptions) {
        viewModelScope.launch {
            setTriggerOnPowerButtonUseCase(status)
        }
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
            hint = buttonsSettingsState.value.latencyUsualMode.toString(),
            range = 100..100000,
            requestKey = EDIT_LATENCY_DIALOG
        )
    }

    fun editVolumeButtonLatencyDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_volume_button_latency),
            message = UIText.StringResource(R.string.button_volume_latency_long),
            hint = buttonsSettingsState.value.latencyVolumeButton.toString(),
            range = 100..100000,
            requestKey = EDIT_VOLUME_LATENCY_DIALOG
        )
    }

    fun editButtonRootLatencyDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_button_latency_root),
            message = UIText.StringResource(R.string.button_latency_long_root),
            hint = buttonsSettingsState.value.latencyRootMode.toString(),
            range = 100..100000,
            requestKey = EDIT_ROOT_LATENCY_DIALOG
        )
    }

    fun showSetTriggerOnVolumeUpButtonDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.destroy_on_button_volume_up_title),
            message = UIText.StringResource(R.string.destroy_on_button_volume_up_long),
            requestKey = TRIGGER_ON_VOLUME_UP_DIALOG
        )
    }

    fun showSetTriggerOnVolumeDownButtonDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.destroy_on_button_volume_down_title),
            message = UIText.StringResource(R.string.destroy_on_button_volume_down_long),
            requestKey = TRIGGER_ON_VOLUME_DOWN_DIALOG
        )
    }

    fun showSetTriggerOnPowerButtonLegacyDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.destroy_on_button_legacy_title),
            message = UIText.StringResource(R.string.destroy_on_button_legacy_long),
            requestKey = TRIGGER_ON_BUTTON_LEGACY_DIALOG
        )
    }

    fun showRunOnDuressPasswordDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_on_password),
            message = UIText.StringResource(R.string.run_on_password_long),
            RUN_ON_PASSWORD_DIALOG
        )
    }

    fun showSetTriggerOnButtonSuperuserDialog() {
        if (permissionsState.value.isRoot) {
            showQuestionDialog(
                title = UIText.StringResource(R.string.destroy_on_button_title),
                message = UIText.StringResource(R.string.destroy_on_button_long),
                requestKey = TRIGGER_ON_BUTTON_SUPERUSER_DIALOG
            )
        } else {
            showInfoDialog(UIText.StringResource(com.sonozaki.resources.R.string.no_superuser_rights), UIText.StringResource(R.string.provide_root))
        }
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

    fun editClicksNumberVolumeDialog() {
        showInputDigitDialog(
            title = UIText.StringResource(R.string.change_clicks_number_volume),
            message = UIText.StringResource(R.string.clicks_number_volume_long),
            hint = buttonsSettingsState.value.volumeButtonAllowedClicks.toString(),
            range = 3..20,
            requestKey = EDIT_CLICK_NUMBER_VOLUME_DIALOG
        )
    }

    fun setRunOnUsbConnection() {
        viewModelScope.launch {
            setUsbSettingsUseCase(UsbSettings.RUN_ON_CONNECTION)
        }
    }

    fun setDoNothingOnUsbConnection() {
        viewModelScope.launch {
            setUsbSettingsUseCase(UsbSettings.DO_NOTHING)
        }
    }

    fun setRunOnDuressPassword(status: Boolean) {
        viewModelScope.launch {
            setRunOnDuressUseCase(status)
        }
    }

    fun setBruteforceProtection(status: Boolean) {
        viewModelScope.launch {
            setBruteForceStatusUseCase(status)
        }
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

    fun showRunOnUSBConnectionDialog() {
        showQuestionDialog(
            title = UIText.StringResource(R.string.run_on_connection),
            message = UIText.StringResource(R.string.run_on_usb_connection_long),
            RUN_ON_USB_DIALOG
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

    companion object {
        const val TRIGGER_ON_BUTTON_SUPERUSER_DIALOG = "trigger_on_button_dialog"
        const val TRIGGER_ON_BUTTON_LEGACY_DIALOG = "trigger_on_button_legacy_dialog"
        const val TRIGGER_ON_VOLUME_UP_DIALOG = "trigger_on_volume_up_dialog"
        const val TRIGGER_ON_VOLUME_DOWN_DIALOG = "trigger_on_volume_down_dialog"
        const val RUN_ON_USB_DIALOG = "usb_dialog"
        const val REBOOT_ON_USB_DIALOG = "reboot_on_usb_dialog"
        const val RUN_ON_PASSWORD_DIALOG = "run_on_password_dialog"
        const val BRUTEFORCE_DIALOG = "bruteforce_dialog"
        const val MAX_PASSWORD_ATTEMPTS_DIALOG = "max_password_attempts"
        const val EDIT_LATENCY_DIALOG = "edit_latency_dialog"
        const val EDIT_ROOT_LATENCY_DIALOG = "edit_root_latency_dialog"
        const val EDIT_VOLUME_LATENCY_DIALOG = "edit_volume_latency_dialog"
        const val EDIT_CLICK_NUMBER_VOLUME_DIALOG = "edit_click_number_volume_dialog"
        const val EDIT_CLICK_NUMBER_DIALOG = "edit_click_number_dialog"
    }
}