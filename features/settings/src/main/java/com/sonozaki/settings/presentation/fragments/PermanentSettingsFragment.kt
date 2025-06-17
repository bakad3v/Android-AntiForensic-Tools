package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import com.sonozaki.dialogs.InputDigitDialog
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.PermanentSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.CHANGE_USER_LIMIT_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.DISABLE_DEV_SETTINGS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.DISABLE_LOGS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.EDIT_BFU_DELAY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.ENABLE_DEV_SETTINGS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.ENABLE_LOGS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.ENABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.LOGD_ON_BOOT_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.LOGD_ON_START_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermanentSettingsVM.Companion.MOVE_TO_BFU_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermanentSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: PermanentSettingsVM by viewModels()
    private var _binding: PermanentSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("PermanentSettingsFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            PermanentSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.permanent_settings)
        setupButtonsAndSwitches()
        listenDeviceProtectionSettings()
        listenDialogs()
    }

    private fun listenDialogs() {
        listenQuestionDialog(
            DISABLE_LOGS_DIALOG
        ) { viewModel.setLogsStatus(false) }
        listenQuestionDialog(
            ENABLE_LOGS_DIALOG
        ) { viewModel.setLogsStatus(true) }
        listenQuestionDialog(
            ENABLE_DEV_SETTINGS_DIALOG
        ) { viewModel.setDevOptionsStatus(true) }
        listenQuestionDialog(
            DISABLE_DEV_SETTINGS_DIALOG
        ) { viewModel.setDevOptionsStatus(false) }
        listenQuestionDialog(
            ENABLE_SAFE_BOOT_RESTRICTION_DIALOG,
        ) {
            viewModel.setSafeBoot(true)
        }
        listenQuestionDialog(
            DISABLE_SAFE_BOOT_RESTRICTION_DIALOG,
        ) {
            viewModel.setSafeBoot(false)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            EDIT_BFU_DELAY_DIALOG
        ) {
                delay ->
            viewModel.changeMoveToBFUDelay(delay)
        }
        InputDigitDialog.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            CHANGE_USER_LIMIT_DIALOG
        ) {
                limit ->
            viewModel.setUserLimit(limit)
        }
        listenQuestionDialog(MOVE_TO_BFU_DIALOG) {
            viewModel.setMoveToBFU(true)
        }
        listenQuestionDialog(
            LOGD_ON_START_DIALOG,
        ) {
            viewModel.setLogdOnStartStatus(true)
        }
        listenQuestionDialog(
            LOGD_ON_BOOT_DIALOG,
        ) {
            viewModel.setLogdOnBootStatus(true)
        }
    }

    private val switchLogdOnStartListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setLogdOnStartStatus(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showLogdOnStartDialog()
    }

    private val switchLogdOnBootListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setLogdOnBootStatus(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showLogdOnBootDialog()
    }

    /**
     * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
     */
    private fun setupButtonsAndSwitches() {
        listenSettings()
        checkPermissions()
        setupClickableElements()
    }

    private fun checkPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                val serviceWorking = viewModel.settingsState.value.serviceWorking
                val rootOrDhizuku = it.isRoot || it.isOwner
                with(binding) {
                    moveToBfu.setSwitchEnabled(rootOrDhizuku && serviceWorking)
                    logdOnBootItem.setSwitchEnabled(it.isRoot)
                    logdOnStartItem.setSwitchEnabled(it.isRoot)
                    setLogsStatus.setActive(it.isRoot)
                    setDeveloperSettingsStatus.setActive(it.isRoot)
                    setSafeBoot.setActive(rootOrDhizuku)
                }
            }
        }
    }

    private fun listenSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsState.collect {
                val permissions = viewModel.permissionsState.value
                val rootOrDhizuku = permissions.isRoot || permissions.isOwner
                with(binding) {
                    moveToBfu.setSwitchEnabled(rootOrDhizuku && it.serviceWorking)
                    logdOnBootItem.setCheckedProgrammatically(
                        it.stopLogdOnBoot,
                        switchLogdOnBootListener
                    )
                    logdOnStartItem.setCheckedProgrammatically(
                        it.stopLogdOnStart,
                        switchLogdOnStartListener
                    )
                }
            }
        }
    }

    private val switchMoveToBFUAutomaticallyListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setMoveToBFU(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.moveDeviceToBFUAutomaticallyDialog()
    }

    private fun listenDeviceProtectionSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.deviceProtectionSettingsState.collect {
                with(binding) {
                    moveToBfuDelay.setDigit(it.rebootDelay.toString())
                    moveToBfu.setCheckedProgrammatically(it.rebootOnLock, switchMoveToBFUAutomaticallyListener)
                }
            }
        }
    }

    private fun setupClickableElements() {
        with(binding) {
            moveToBfuDelay.setOnClickListener {
                viewModel.showMovingToBFUDelayDialog()
            }
            usersLimit.setOnClickListener {
                viewModel.showChangeUserLimitDialog()
            }
            setSafeBoot.setOnClickListener {
                viewModel.changeSafeBootRestrictionSettingsDialog()
            }
            setDeveloperSettingsStatus.setOnClickListener {
                viewModel.changeDeveloperSettingsStatusDialog()
            }
            setLogsStatus.setOnClickListener {
                viewModel.changeLogsStatusDialog()
            }
            setupLogsManually.setOnClickListener {

            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}