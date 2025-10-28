package com.sonozaki.settings.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.MultiuserSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.DISABLE_MULTIUSER_UI_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.DISABLE_MULTIUSER_UI_ON_BOOT
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.DISABLE_MULTIUSER_UI_ON_LOCK
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.DISABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.DISABLE_USER_SWITCHER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.ENABLE_MULTIUSER_UI_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.ENABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.ENABLE_USER_SWITCHER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.MultiuserSettingsVM.Companion.OPEN_MULTIUSER_SETTINGS_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultiuserSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: MultiuserSettingsVM by viewModels()
    private var _binding: MultiuserSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("MultiuserSettingsFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            MultiuserSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.multiuser_settings)
        setupDisableMultiuserUIMenu()
        listenDialogResults()
        setupButtonsAndSwitches()
        listenDeviceProtectionSettings()
    }

    /**
     * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
     */
    private fun setupButtonsAndSwitches() {
        listenSettings()
        checkPermissions()
        setupClickableElements()
    }

    private fun multiuserUiProtectionDescription(multiuserUIProtection: MultiuserUIProtection): String {
        return when(multiuserUIProtection) {
            MultiuserUIProtection.NEVER -> requireContext().getString(R.string.never)
            MultiuserUIProtection.ON_REBOOT -> requireContext().getString(R.string.device_booted)
            MultiuserUIProtection.ON_SCREEN_OFF -> requireContext().getString(R.string.device_locked)
        }
    }

    private fun listenDeviceProtectionSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.deviceProtectionSettingsState.collect {
                with(binding) {
                    disableMultiuserUi.setText(multiuserUiProtectionDescription(it.multiuserUIProtection))
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
                    disableMultiuserUi.isClickable = (rootOrDhizuku || permissions.isShizuku) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && it.serviceWorking
                }
            }
        }
    }

    private fun checkPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                val serviceWorking = viewModel.settingsState.value.serviceWorking
                val rootOrDhizuku = it.isRoot || it.isOwner
                val rootOrShizuku = it.isRoot || it.isShizuku
                with(binding) {
                    disableMultiuserUi.isClickable = (rootOrDhizuku || it.isShizuku) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && serviceWorking
                    setMultiuserUi.setActive(it.isRoot)
                    setUserSwitcherUi.setActive(
                        rootOrShizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    )
                    switchUserPermission.setActive(
                        rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    )
                }
            }
        }
    }

    private fun setupClickableElements() {
        with(binding) {
            setMultiuserUi.setOnClickListener {
                viewModel.changeMultiuserUISettingsDialog()
            }
            setUserSwitcherUi.setOnClickListener {
                viewModel.changeUserSwitcherDialog()
            }
            switchUserPermission.setOnClickListener {
                viewModel.changeSwitchUserDialog()
            }
        }
    }

    private fun setupDisableMultiuserUIMenu() {
        binding.disableMultiuserUi.setOnClickListener {
            changeDisableMultiuserUIMenu()
        }
    }

    /**
     * Changing disable multiuser UI strategy
     */
    private fun changeDisableMultiuserUIMenu() {
        val popup = PopupMenu(context, binding.disableMultiuserUi.menu)
        popup.menuInflater.inflate(R.menu.multiuser_ui_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.never -> {
                    viewModel.setMultiUserUIProtection(MultiuserUIProtection.NEVER)
                }
                R.id.device_booted -> {
                    viewModel.disableMultiuserUIOnBootDialog()
                }

                R.id.device_locked-> {
                    viewModel.disableMultiuserUIonLockDialog()
                }
                else -> throw RuntimeException("Wrong multiuser UI setting")
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    private fun listenDialogResults() {
        listenQuestionDialog(
            OPEN_MULTIUSER_SETTINGS_DIALOG,
        ) {
            createProfile()
        }
        listenQuestionDialog(
            ENABLE_MULTIUSER_UI_DIALOG,
        ) {
            viewModel.setMultiuserUI(true)
        }
        listenQuestionDialog(
            DISABLE_MULTIUSER_UI_DIALOG,
        ) {
            viewModel.setMultiuserUI(false)
        }
        listenQuestionDialog(
            ENABLE_USER_SWITCHER_DIALOG,
        ) {
            viewModel.setUserSwitcher(true)
        }
        listenQuestionDialog(
            DISABLE_USER_SWITCHER_DIALOG,
        ) {
            viewModel.setUserSwitcher(false)
        }
        listenQuestionDialog(
            ENABLE_SWITCH_USER_RESTRICTION_DIALOG,
        ) {
            viewModel.setUserSwitchRestriction(true)
        }
        listenQuestionDialog(
            DISABLE_SWITCH_USER_RESTRICTION_DIALOG,
        ) {
            viewModel.setUserSwitchRestriction(false)
        }
        listenQuestionDialog(DISABLE_MULTIUSER_UI_ON_BOOT) {
            viewModel.setMultiUserUIProtection(MultiuserUIProtection.ON_REBOOT)
        }
        listenQuestionDialog(DISABLE_MULTIUSER_UI_ON_LOCK) {
            viewModel.setMultiUserUIProtection(MultiuserUIProtection.ON_SCREEN_OFF)
        }
    }

    private fun createProfile() {
        try {
            startActivity(Intent("android.settings.USER_SETTINGS"))
        } catch (_: ActivityNotFoundException) {
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}