package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.sonozaki.dialogs.InputDigitDialog
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.entities.UsbSettings
import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.TriggerSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.BRUTEFORCE_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.EDIT_CLICK_NUMBER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.EDIT_CLICK_NUMBER_VOLUME_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.EDIT_LATENCY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.EDIT_ROOT_LATENCY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.EDIT_VOLUME_LATENCY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.MAX_PASSWORD_ATTEMPTS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.REBOOT_ON_USB_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.RUN_ON_PASSWORD_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.RUN_ON_USB_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.TRIGGER_ON_BUTTON_LEGACY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.TRIGGER_ON_BUTTON_SUPERUSER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.TRIGGER_ON_VOLUME_DOWN_DIALOG
import com.sonozaki.settings.presentation.viewmodel.TriggerSettingsVM.Companion.TRIGGER_ON_VOLUME_UP_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriggerSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: TriggerSettingsVM by viewModels()
    private var _binding: TriggerSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("TriggerSettingsFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            TriggerSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.triggers_settings)
        setupUsbMenu()
        setupPowerButtonMenu()
        setupVolumeButtonMenu()
        listenDialogResults()
        setupButtonsAndSwitches()
    }

    private val switchRunOnDuressPasswordListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setRunOnDuressPassword(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showRunOnDuressPasswordDialog()
    }

    private val switchBruteforceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setBruteforceProtection(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showBruteforceDialog()
    }

    private fun setupButtonsAndSwitches() {
        listenSettings()
        listenButtonSettings()
        listenBruteforceProtectionSettings()
        checkPermissions()
        listenUsbSettings()
        setupClickableElements()
    }

    private fun setupClickableElements() {
        with(binding) {
            latency.setOnClickListener {
                viewModel.editButtonLatencyDialog()
            }
            rootLatency.setOnClickListener {
                viewModel.editButtonRootLatencyDialog()
            }
            clicksNumber.setOnClickListener {
                viewModel.editClicksNumberDialog()
            }
            clicksNumberVolume.setOnClickListener {
                viewModel.editClicksNumberVolumeDialog()
            }
            latencyVolume.setOnClickListener {
                viewModel.editVolumeButtonLatencyDialog()
            }
            allowedAttempts.setOnClickListener {
                viewModel.editMaxPasswordAttemptsDialog()
            }
        }
    }

    private fun listenUsbSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.usbSettingState.collect {
                val textId = when(it) {
                    UsbSettings.DO_NOTHING -> R.string.do_nothing
                    UsbSettings.RUN_ON_CONNECTION -> R.string.destroy_data
                    UsbSettings.REBOOT_ON_CONNECTION -> R.string.reboot
                }
                binding.usbMenu.setText(requireContext().getString(textId))
            }
        }
    }

    private fun checkPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                with(binding) {
                    bruteforceItem.setSwitchEnabled(it.isAdmin)
                }
            }
        }
    }



    private fun listenBruteforceProtectionSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.bruteforceProtectionState.collect {
                with(binding) {
                    allowedAttempts.setDigit(it.allowedAttempts.toString())
                    bruteforceItem.setCheckedProgrammatically(
                        it.bruteforceRestricted,
                        switchBruteforceListener
                    )
                }
            }
        }
    }

    private fun listenSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsState.collect {
                with(binding) {
                    runOnPasswordItem.setSwitchEnabled(it.serviceWorking)
                    powerButtonItem.isClickable = it.serviceWorking
                    volumeButtonItem.isClickable = it.serviceWorking
                    runOnPasswordItem.setCheckedProgrammatically(
                        it.runOnDuressPassword,
                        switchRunOnDuressPasswordListener
                    )
                    usbMenu.isClickable = it.serviceWorking
                }
            }
        }
    }

    private fun getPowerButtonSettingsDescription(triggerOptions: PowerButtonTriggerOptions): String {
        val id = when(triggerOptions) {
            PowerButtonTriggerOptions.IGNORE -> R.string.do_nothing
            PowerButtonTriggerOptions.DEPRECATED_WAY -> R.string.run_on_power_clicks_legacy
            PowerButtonTriggerOptions.SUPERUSER_WAY -> R.string.run_on_power_clicks_superuser
        }
        return requireContext().getString(id)
    }

    private fun getVolumeButtonSettingsDescription(volumeButtonTriggerOptions: VolumeButtonTriggerOptions): String {
        val id = when(volumeButtonTriggerOptions) {
            VolumeButtonTriggerOptions.IGNORE -> R.string.do_nothing
            VolumeButtonTriggerOptions.ON_VOLUME_UP -> R.string.run_on_volume_up
            VolumeButtonTriggerOptions.ON_VOLUME_DOWN -> R.string.run_on_volume_down
        }
        return requireContext().getString(id)
    }

    private fun listenButtonSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.buttonsSettingsState.collect {
                with(binding) {
                    rootLatency.setDigit(it.latencyRootMode.toString())
                    latency.setDigit(it.latencyUsualMode.toString())
                    clicksNumber.setDigit(it.allowedClicks.toString())
                    latencyVolume.setDigit(it.latencyVolumeButton.toString())
                    clicksNumberVolume.setDigit(it.volumeButtonAllowedClicks.toString())
                    powerButtonItem.setText(getPowerButtonSettingsDescription(it.triggerOnButton))
                    volumeButtonItem.setText(getVolumeButtonSettingsDescription(it.triggerOnVolumeButton))
                }
            }
        }
    }

    private fun setupVolumeButtonMenu() {
        binding.volumeButtonItem.setOnClickListener {
            changeVolumeButtonClicksMenu()
        }
    }

    private fun setupPowerButtonMenu() {
        binding.powerButtonItem.setOnClickListener {
            changePowerButtonClicksMenu()
        }
    }

    private fun setupUsbMenu() {
        binding.usbMenu.setOnClickListener {
            showUsbMenu()
        }
    }

    private fun changeVolumeButtonClicksMenu() {
        val popup = PopupMenu(context, binding.volumeButtonItem.menu)
        popup.menuInflater.inflate(R.menu.volume_button_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.do_nothing_volume -> {
                    viewModel.setTriggerOnVolumeButton(VolumeButtonTriggerOptions.IGNORE)
                }
                R.id.on_volume_up -> {
                    viewModel.showSetTriggerOnVolumeUpButtonDialog()
                }

                R.id.on_volume_down -> {
                    viewModel.showSetTriggerOnVolumeDownButtonDialog()
                }
                else -> throw RuntimeException("Wrong multiuser UI setting")
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    /**
     * Changing power button menu trigger reaction
     */
    private fun changePowerButtonClicksMenu() {
        val popup = PopupMenu(context, binding.powerButtonItem.menu)
        popup.menuInflater.inflate(R.menu.power_button_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.do_nothing_power -> {
                    viewModel.setTriggerOnButton(PowerButtonTriggerOptions.IGNORE)
                }
                R.id.on_power_clicks_legacy -> {
                    viewModel.showSetTriggerOnPowerButtonLegacyDialog()
                }

                R.id.on_power_clicks_superuser-> {
                    viewModel.showSetTriggerOnButtonSuperuserDialog()
                }
                else -> throw RuntimeException("Wrong multiuser UI setting")
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    /**
     * Changing USB settings
     */
    private fun showUsbMenu() {
        val popup = PopupMenu(context, binding.usbMenu.menu)
        popup.menuInflater.inflate(R.menu.usb_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.do_nothing -> {
                    viewModel.setDoNothingOnUsbConnection()
                }

                R.id.run_on_connection -> {
                    viewModel.showRunOnUSBConnectionDialog()
                }

                R.id.reboot_on_connection -> {
                    viewModel.showRebootOnUSBDialog()
                }

                else -> throw RuntimeException("Wrong menu item")
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    /**
     * Listening for dialog result
     */
    private fun listenDialogResults() {
        listenQuestionDialog(
            TRIGGER_ON_BUTTON_SUPERUSER_DIALOG
        ) { viewModel.setTriggerOnButton(PowerButtonTriggerOptions.SUPERUSER_WAY) }
        listenQuestionDialog(
            TRIGGER_ON_VOLUME_UP_DIALOG
        ) { viewModel.setTriggerOnVolumeButton(VolumeButtonTriggerOptions.ON_VOLUME_UP) }

        listenQuestionDialog(
            TRIGGER_ON_VOLUME_DOWN_DIALOG
        ) { viewModel.setTriggerOnVolumeButton(VolumeButtonTriggerOptions.ON_VOLUME_DOWN) }

        listenQuestionDialog(
            TRIGGER_ON_BUTTON_LEGACY_DIALOG
        ) {
            viewModel.setTriggerOnButton(PowerButtonTriggerOptions.DEPRECATED_WAY)
        }
        listenQuestionDialog(
            RUN_ON_USB_DIALOG,
        ) {
            viewModel.setRunOnUsbConnection()
        }
        listenQuestionDialog(
            BRUTEFORCE_DIALOG,
        ) {
            viewModel.setBruteforceProtection(true)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            MAX_PASSWORD_ATTEMPTS_DIALOG
        ) { limit ->
            viewModel.setBruteForceLimit(limit)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_LATENCY_DIALOG
        ) {
                latency ->
            viewModel.setLatency(latency)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_VOLUME_LATENCY_DIALOG
        ) {
                latency ->
            viewModel.setVolumeLatency(latency)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_ROOT_LATENCY_DIALOG
        ) {
                latency ->
            viewModel.setRootLatency(latency)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_CLICK_NUMBER_DIALOG
        ) {
                clicks ->
            viewModel.setClicksNumber(clicks)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_CLICK_NUMBER_VOLUME_DIALOG
        ) {
            viewModel.setClicksNumberVolume(it)
        }
        listenQuestionDialog(
            RUN_ON_PASSWORD_DIALOG,
        ) {
            viewModel.setRunOnDuressPassword(true)
        }
        listenQuestionDialog(
            REBOOT_ON_USB_DIALOG
        ) {
            viewModel.setRebootOnUSB()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}