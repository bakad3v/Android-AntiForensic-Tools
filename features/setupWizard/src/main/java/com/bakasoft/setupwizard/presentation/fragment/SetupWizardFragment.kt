package com.bakasoft.setupwizard.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bakasoft.setupwizard.R
import com.bakasoft.setupwizard.databinding.WizardScreenBinding
import com.bakasoft.setupwizard.domain.entities.AppState
import com.bakasoft.setupwizard.domain.entities.DataSelected
import com.bakasoft.setupwizard.domain.entities.PermissionsState
import com.bakasoft.setupwizard.domain.entities.SettingsElementState
import com.bakasoft.setupwizard.domain.entities.SetupWizardState
import com.bakasoft.setupwizard.domain.entities.WizardElement
import com.bakasoft.setupwizard.domain.routers.SetupWizardRouter
import com.bakasoft.setupwizard.presentation.viewmodel.SetupWizardVM
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import java.util.EnumMap
import javax.inject.Inject


@AndroidEntryPoint
class SetupWizardFragment: Fragment() {
    private val binding: WizardScreenBinding get() = _binding?: throw RuntimeException("SetupWizardFragmentBinding == null")
    private var _binding: WizardScreenBinding? = null
    private val viewModel: SetupWizardVM by viewModels()

    @Inject
    lateinit var setupWizardRouter: SetupWizardRouter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            WizardScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMainActivityState()
        setupHelp()
        setupButtons()
        listenState()
    }

    private fun listenState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.wizardSate.collect { state ->
                when(state) {
                    is SetupWizardState.Loading -> {
                        showLoading(true)
                    }
                    is SetupWizardState.Data -> {
                        showLoading(false)
                        setupColors(state.dataMap)
                        setupClickListeners(state.dataSelected)
                        setupWizards(state.triggersFixActive, state.protectionFixActive)
                        setupButtonTexts(state.dataMap, state.dataSelected, state.permissionsState)
                        setupAppState(state.state)
                    }
                }
            }
        }
    }

    private fun setupAppState(state: AppState) {
        with(binding) {
            appStateSubtitle.movementMethod = LinkMovementMethod.getInstance()
            when(state) {
                AppState.NICE -> {
                    appStateImage.setImageResource(R.drawable.rena_ok)
                    appState.text = requireContext().getString(R.string.everything_ok)
                    appStateSubtitle.text = HtmlCompat.fromHtml(requireContext().getString(R.string.all_requirements_satisafied),
                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
                AppState.WITH_WARNINGS -> {
                    appStateImage.setImageResource(R.drawable.rena_sad)
                    appState.text = requireContext().getString(R.string.some_warnings)
                    appStateSubtitle.text = requireContext().getString(R.string.some_warnings_long)
                }
                AppState.BAD -> {
                    appStateImage.setImageResource(R.drawable.rena_angry)
                    appState.text = requireContext().getString(R.string.unsafe)
                    appStateSubtitle.text = requireContext().getString(R.string.unsafe_long)
                }
            }
        }
    }

    private fun setupButtonTexts(map: EnumMap<WizardElement, SettingsElementState>,
                                 dataSelected: DataSelected, permissionsState: PermissionsState) {
        with(binding) {
            val appVersionSettings = map.getOrDefault(WizardElement.CORRECT_APP_VERSION,
                SettingsElementState.UNKNOW)
            when(appVersionSettings) {
                SettingsElementState.OK -> {
                    installRequiredAppVersion.setText(requireContext().getString(R.string.latest_app_version_installed))
                }
                SettingsElementState.REQUIRED -> {
                    installRequiredAppVersion.setText(requireContext().getString(R.string.install_testonly_version))
                }
                SettingsElementState.RECOMMENDED -> {
                    installRequiredAppVersion.setText(requireContext().getString(R.string.please_update_app))
                }
                SettingsElementState.NOT_NEEDED -> {}
                SettingsElementState.UNKNOW -> {
                    installRequiredAppVersion.setText(requireContext().getString(R.string.cant_check_updates))
                }
            }

            val usbSettings = map.getOrDefault(WizardElement.TRIGGER_ON_USB,
                SettingsElementState.UNKNOW)

            when(usbSettings) {
                SettingsElementState.NOT_NEEDED, SettingsElementState.UNKNOW -> {}
                SettingsElementState.REQUIRED, SettingsElementState.OK ->
                    triggerOnUsbConnection.setText(requireContext().getString(R.string.trigger_on_usb_connection))
                SettingsElementState.RECOMMENDED ->
                    triggerOnUsbConnection.setText(requireContext().getString(R.string.reboot_on_usb_selected))
            }

            val buttonSettings = map.getOrDefault(WizardElement.TRIGGER_ON_CLICKS,
                SettingsElementState.UNKNOW)

            when(buttonSettings) {
                SettingsElementState.NOT_NEEDED, SettingsElementState.UNKNOW -> {}
                SettingsElementState.REQUIRED, SettingsElementState.OK ->
                    triggerOnButton.setText(requireContext().getString(R.string.trigger_on_button))
                SettingsElementState.RECOMMENDED ->
                    triggerOnButton.setText(requireContext().getString(R.string.legacy_way_of_click_detection))
            }

            when(permissionsState) {
                PermissionsState.PERFECT, PermissionsState.NOT_ENOUGH ->
                    grantSuperuserPermissions.setText(requireContext().getString(R.string.grant_superuser_permissions))
                PermissionsState.PROBABLY_NOT_ENOUGH ->
                    grantSuperuserPermissions.setText(requireContext().getString(R.string.some_permissions_granted))
                PermissionsState.PROBABLY_ENOUGH ->
                    grantSuperuserPermissions.setText(requireContext().getString(R.string.probably_enough_permissions))
            }

            val safeBootSettings = map.getOrDefault(WizardElement.DISABLE_SAFE_BOOT,
                SettingsElementState.UNKNOW)

            when(safeBootSettings) {
                SettingsElementState.NOT_NEEDED, SettingsElementState.UNKNOW,
                SettingsElementState.REQUIRED, SettingsElementState.OK ->
                    disableSafeBoot.setText(requireContext().getString(R.string.disable_safe_boot))
                SettingsElementState.RECOMMENDED ->
                    disableSafeBoot.setText(requireContext().getString(R.string.not_enough_rights_to_check_safe_boot))
            }

            val logsSettings = map.getOrDefault(WizardElement.DISABLE_LOGS,
                SettingsElementState.UNKNOW)

            when(logsSettings) {
                SettingsElementState.NOT_NEEDED, SettingsElementState.UNKNOW,
                SettingsElementState.REQUIRED, SettingsElementState.OK ->
                    disableLogs.setText(requireContext().getString(R.string.disable_logs))
                SettingsElementState.RECOMMENDED ->
                    disableLogs.setText(requireContext().getString(R.string.not_enough_rights_to_check_logs))
            }

            when(dataSelected) {
                DataSelected.PROFILES -> {
                    selectData.setText(requireContext().getString(R.string.profile_deletion_selected))
                }
                DataSelected.FILES-> {
                    selectData.setText(requireContext().getString(R.string.files_deletion_selected))
                }
                DataSelected.WIPE -> {
                    selectData.setText(requireContext().getString(R.string.wipe_data_selected))
                }
                DataSelected.ROOT -> {
                    selectData.setText(requireContext().getString(R.string.root_selected))
                }
                DataSelected.NONE ->  {
                    selectData.setText(requireContext().getString(R.string.select_data))
                }
            }
        }
    }

    private fun setupWizards(triggersFixActive: Boolean, protectionFixActive: Boolean) {
        with(binding) {
            setupTriggersAutomatically.isEnabled = triggersFixActive
            setupTriggersAutomatically.setOnClickListener {
                findNavController().navigate(R.id.action_setupWizardFragment_to_setupTriggersConfirmationFragment)
            }
            setupProtectionAutomatically.isEnabled = protectionFixActive
            setupProtectionAutomatically.setOnClickListener {
                findNavController().navigate(R.id.action_setupWizardFragment_to_setupProtectionConfirmationFragment)
            }
        }
    }

    private fun setupClickListeners(dataSelected: DataSelected) {
        with(binding) {
            val link = when (dataSelected) {
                DataSelected.NONE, DataSelected.PROFILES -> PROFILES_DESTRUCTION_HELP
                DataSelected.WIPE -> DATA_DESTRUCTION_HELP
                DataSelected.FILES -> FILES_DESTRUCTION_HELP
                DataSelected.ROOT -> ROOT_COMMANDS_HELP
            }
            destroyDataHelp.setOnClickListener {
                openLink(link)
            }
            selectData.setOnClickListener {
                when(dataSelected) {
                    DataSelected.NONE, DataSelected.PROFILES -> setupWizardRouter.openProfiles(findNavController())
                    DataSelected.WIPE -> setupWizardRouter.openSettings(findNavController())
                    DataSelected.FILES -> setupWizardRouter.openFiles(findNavController())
                    DataSelected.ROOT -> setupWizardRouter.openRoot(findNavController())
                }
            }
            activateDataDestruction.setOnClickListener {
                when(dataSelected) {
                    DataSelected.NONE, DataSelected.PROFILES -> setupWizardRouter.openProfiles(findNavController())
                    DataSelected.WIPE -> setupWizardRouter.openSettings(findNavController())
                    DataSelected.FILES -> setupWizardRouter.openFiles(findNavController())
                    DataSelected.ROOT -> setupWizardRouter.openRoot(findNavController())
                }
            }
        }
    }

    private fun setupColors(dataMap: EnumMap<WizardElement, SettingsElementState>) {
        with(binding) {
            installRequiredAppVersion.setState(dataMap.getOrDefault(WizardElement.CORRECT_APP_VERSION,
                SettingsElementState.UNKNOW))
            provideAccessibilityService.setState(dataMap.getOrDefault(WizardElement.ACCESSIBILITY_SERVICE,
                SettingsElementState.UNKNOW))
            grantSuperuserPermissions.setState(dataMap.getOrDefault(WizardElement.SUPERUSER_PERMISSIONS,
                SettingsElementState.UNKNOW))
            triggerOnUsbConnection.setState(dataMap.getOrDefault(WizardElement.TRIGGER_ON_USB,
                SettingsElementState.UNKNOW))
            triggerOnDuressPassword.setState(dataMap.getOrDefault(WizardElement.TRIGGER_ON_PASSWORD,
                SettingsElementState.UNKNOW))
            triggerOnButton.setState(dataMap.getOrDefault(WizardElement.TRIGGER_ON_CLICKS,
                SettingsElementState.UNKNOW))
            triggerOnBruteforce.setState(dataMap.getOrDefault(WizardElement.TRIGGER_ON_BRUTEFORCE,
                SettingsElementState.UNKNOW))
            removeItself.setState(dataMap.getOrDefault(WizardElement.ENABLE_SELF_DESTRUCTION,
                SettingsElementState.UNKNOW))
            hideNotifications.setState(
                dataMap.getOrDefault(WizardElement.HIDE_NOTIFICATIONS,
                    SettingsElementState.UNKNOW)
            )
            hideApp.setState(dataMap.getOrDefault(WizardElement.ENABLE_HIDING,
                SettingsElementState.UNKNOW))
            disableLogs.setState(dataMap.getOrDefault(WizardElement.DISABLE_LOGS,
                SettingsElementState.UNKNOW))
            disableSafeBoot.setState(dataMap.getOrDefault(WizardElement.DISABLE_SAFE_BOOT,
                SettingsElementState.UNKNOW))
            setupMultiuserUiHiding.setState(dataMap.getOrDefault(WizardElement.SETUP_MULTIUSER,
                SettingsElementState.UNKNOW))
            runTrim.setState(dataMap.getOrDefault(WizardElement.ACTIVATE_TRIM,
                SettingsElementState.UNKNOW))
            selectData.setState(dataMap.getOrDefault(WizardElement.SELECT_DATA,
                SettingsElementState.UNKNOW))
            activateDataDestruction.setState(dataMap.getOrDefault(WizardElement.ACTIVATE_DESTRUCTION,
                SettingsElementState.UNKNOW))
        }
    }

    private fun showLoading(loading: Boolean) {
        with(binding) {
            dataLayout.isVisible = !loading
            loadingLayout.isVisible = loading
        }
    }

    private fun setupButtons() {
        with(binding) {
            installRequiredAppVersion.setOnClickListener {
                setupWizardRouter.openUpdateCenter(findNavController())
            }
            provideAccessibilityService.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            grantSuperuserPermissions.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            triggerOnUsbConnection.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            triggerOnDuressPassword.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            triggerOnButton.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            triggerOnBruteforce.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            removeItself.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            hideNotifications.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            hideApp.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            disableLogs.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            disableSafeBoot.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            setupMultiuserUiHiding.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
            runTrim.setOnClickListener {
                setupWizardRouter.openSettings(findNavController())
            }
        }
    }

    private fun setupHelp() {
        with(binding) {
            installAppHelp.setOnClickListener {
                openLink(APP_VERSION_HELP)
            }
            permissionsHelp.setOnClickListener {
                openLink(PERMISSIONS_HELP)
            }
            triggersHelp.setOnClickListener {
                openLink(TRIGGERS_HELP)
            }
            protectionHelp.setOnClickListener {
                openLink(ANTIFORENSIC_PROTECTION_HELP)
            }
            notificationsHelp.setOnClickListener {
                openLink(NOTIFICATION_SETTINGS_HELP)
            }
        }
    }

    private fun openLink(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri())
        startActivity(browserIntent)
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            activity.setActivityState(ActivityState.NormalActivityState(requireContext().getString(R.string.setup_wizard)))
        }
    }

    companion object {
        private const val APP_VERSION_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools?tab=readme-ov-file#apps-versions"
        private const val PERMISSIONS_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Permissions-settings"
        private const val PROFILES_DESTRUCTION_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Profiles-settings"
        private const val FILES_DESTRUCTION_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Files-deletion-settings"
        private const val DATA_DESTRUCTION_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Data-destruction-settings"
        private const val ROOT_COMMANDS_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Root-commands-settings"
        private const val TRIGGERS_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Triggers-settings"
        private const val ANTIFORENSIC_PROTECTION_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Data-destruction-settings"
        private const val NOTIFICATION_SETTINGS_HELP = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Notifications-hiding"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}