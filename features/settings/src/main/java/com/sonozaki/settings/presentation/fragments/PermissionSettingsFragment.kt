package com.sonozaki.settings.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.PermissionsSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.PermissionSettingsVM
import com.sonozaki.settings.presentation.viewmodel.PermissionSettingsVM.Companion.INSTALL_DIZUKU_DIALOG
import com.sonozaki.settings.presentation.viewmodel.PermissionSettingsVM.Companion.MOVE_TO_ACCESSIBILITY_SERVICE
import com.sonozaki.settings.presentation.viewmodel.PermissionSettingsVM.Companion.ROOT_WARNING_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: PermissionSettingsVM by viewModels()
    private var _binding: PermissionsSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("PermissionsSettingsFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            PermissionsSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.permissions_setting)
        listenDialogResults()
        setupButtonsAndSwitches()
    }

    private fun requestAdminRights() {
        startActivity(viewModel.adminRightsIntent())
    }

    private val switchAccessibilityServiceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        switch.isChecked = !checked
        viewModel.showAccessibilityServiceDialog()
    }

    private val switchRootListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.showRootDisableDialog()
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showRootWarningDialog()
    }

    private val switchAdminListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.disableAdmin()
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        requestAdminRights()
    }

    private val switchDhizukuListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        switch.isChecked = !checked
        viewModel.askDhizuku()
    }

    /**
     * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
     */
    private fun setupButtonsAndSwitches() {
        listenSettings()
        checkPermissions()
    }

    private fun listenSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsState.collect {
                with(binding) {
                    accessibilityServiceItem.setCheckedProgrammatically(
                        it.serviceWorking,
                        switchAccessibilityServiceListener
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                with(binding) {
                    rootItem.setCheckedProgrammatically(it.isRoot, switchRootListener)
                    adminRightsItem.setCheckedProgrammatically(it.isAdmin, switchAdminListener)
                    dhizukuItem.setCheckedProgrammatically(it.isOwner, switchDhizukuListener)
                }
            }
        }
    }

    private fun openDhizukuLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, "https://github.com/iamr0s/Dhizuku".toUri())
        startActivity(browserIntent)
    }

    private fun startAccessibilityService() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun listenDialogResults() {
        listenQuestionDialog(
            INSTALL_DIZUKU_DIALOG,
        ) {
            openDhizukuLink()
        }
        listenQuestionDialog(
            MOVE_TO_ACCESSIBILITY_SERVICE,
        ) {
            startAccessibilityService()
        }
        listenQuestionDialog(
            ROOT_WARNING_DIALOG
        ) {
            viewModel.askRoot()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}