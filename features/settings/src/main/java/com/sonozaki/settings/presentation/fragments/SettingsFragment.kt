package com.sonozaki.settings.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.dialogs.DialogLauncher
import com.sonozaki.dialogs.InputDigitDialog
import com.sonozaki.entities.MultiuserUIProtection
import com.sonozaki.entities.Theme
import com.sonozaki.entities.UsbSettings
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsFragmentBinding
import com.sonozaki.settings.domain.routers.SettingsRouter
import com.sonozaki.settings.presentation.actions.SettingsAction
import com.sonozaki.settings.presentation.viewmodel.SettingsVM
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ALLOW_SCREENSHOTS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.BRUTEFORCE_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.CHANGE_USER_LIMIT_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.CLEAR_DATA_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.CLEAR_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DESTROY_DATA_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_MULTIUSER_UI_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_MULTIUSER_UI_ON_BOOT
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_MULTIUSER_UI_ON_LOCK
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.DISABLE_USER_SWITCHER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.EDIT_BFU_DELAY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.EDIT_CLICK_NUMBER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.EDIT_LATENCY_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ENABLE_MULTIUSER_UI_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ENABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ENABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ENABLE_USER_SWITCHER_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.HIDE_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.INSTALL_DIZUKU_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.LOGD_ON_BOOT_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.LOGD_ON_START_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.MAX_PASSWORD_ATTEMPTS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.MOVE_TO_ACCESSIBILITY_SERVICE
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.MOVE_TO_ADMIN_SETTINGS
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.MOVE_TO_BFU_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.OPEN_MULTIUSER_SETTINGS_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.REBOOT_ON_USB_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.ROOT_WARNING_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.RUN_ON_PASSWORD_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.RUN_ON_USB_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.SELF_DESTRUCTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.TRIGGER_ON_BUTTON_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.TRIM_DIALOG
import com.sonozaki.settings.presentation.viewmodel.SettingsVM.Companion.WIPE_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Fragment for managing app settings
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

  private val viewModel: SettingsVM by viewModels()
  private var _binding: SettingsFragmentBinding? = null
  private val binding
    get() = _binding ?: throw RuntimeException("SettingsFragmentBinding == null")
  private val dialogLauncher by lazy {
      DialogLauncher(
          parentFragmentManager,
          context
      )
  }

  @Inject
  lateinit var router: SettingsRouter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding =
      SettingsFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupActivity()
    setupThemesMenu()
    setupUsbMenu()
    setupDisableMultiuserUIMenu()
    listenDialogResults()
    setupDialogs()
    setupMenu()
    setupButtonsAndSwitches()
  }

  private fun setupDisableMultiuserUIMenu() {
    binding.disableMultiuserUi.setOnClickListener {
      changeDisableMultiuserUIMenu()
    }
  }

  private fun setupUsbMenu() {
    binding.usbMenu.setOnClickListener {
      showUsbMenu()
    }
  }

  /**
   * Setting up faq icon in action bar
   */
  private fun setupMenu() {
    requireActivity().addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.faq_menu, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
          R.id.help -> viewModel.showFaq()
        }
        return true
      }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
  }

  private fun startAccessibilityService() {
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
  }

  private fun requestAdminRights() {
    startActivity(viewModel.adminRightsIntent())
  }

  private val switchWipeListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setWipe(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showWipeDialog()
  }

  private val switchTrimListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRunTRIM(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showTRIMDialog()
  }

  private val switchBruteforceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setBruteforceProtection(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showBruteforceDialog()
  }

  private val switchSelfDestructListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRemoveItself(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showSelfDestructionDialog()
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

  private val switchAccessibilityServiceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    switch.isChecked = !checked
    viewModel.showAccessibilityServiceDialog()
  }

  private val switchDhizukuListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    switch.isChecked = !checked
    viewModel.askDhizuku()
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

  private val switchHideListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setHide(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showHideDialog()
  }

  private val switchScreenshotsListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setScreenShotsStatus(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showAllowScreenShotsDialog()
  }

  private val switchClearListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setClear(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showClearDialog()
  }

  private val switchClearDataListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setClearData(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showClearDataDialog()
  }

  private val switchRunOnDuressPasswordListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRunOnDuressPassword(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showRunOnDuressPasswordDialog()
  }

  private val switchTriggerOnButtonListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setTriggerOnButton(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showSetTriggerOnButtonDialog()
  }

  private val switchMoveToBFUAutomaticallyListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setMoveToBFU(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.moveDeviceToBFUAutomaticallyDialog()
  }



  /**
   * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
   */
  private fun setupButtonsAndSwitches() {
    listenPopup()
    listenSettings()
    listenButtonSettings()
    listenBruteforceProtectionSettings()
    checkPermissions()
    listenUsbSettings()
    listenDeviceProtectionSettings()
    setupClickableElements()
  }

  private fun listenPopup() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.updatePopupStatusFlow.collect {
        with(binding) {
          testOnlyUpdateAlert.isVisible = it
        }
      }
    }
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
          moveToBfuDelay.setDigit(it.rebootDelay.toString())
          moveToBfu.setCheckedProgrammatically(it.rebootOnLock, switchMoveToBFUAutomaticallyListener)
        }
      }
    }
  }

  private fun setupClickableElements() {
    with(binding) {
      installTestonlyUpdate.setOnClickListener {
        viewModel.updateApp()
      }
      ignoreTestonlyUpdate.setOnClickListener {
        viewModel.disableUpdatePopup()
      }
      moveToBfuDelay.setOnClickListener {
        viewModel.showMovingToBFUDelayDialog()
      }
      usersLimit.setOnClickListener {
        viewModel.showChangeUserLimitDialog()
      }
      latency.setOnClickListener {
        viewModel.editButtonLatencyDialog()
      }
      clicksNumber.setOnClickListener {
        viewModel.editClicksNumberDialog()
      }
      setMultiuserUi.setOnClickListener {
        viewModel.changeMultiuserUISettingsDialog()
      }
      setSafeBoot.setOnClickListener {
        viewModel.changeSafeBootRestrictionSettingsDialog()
      }
      setUserSwitcherUi.setOnClickListener {
        viewModel.changeUserSwitcherDialog()
      }
      switchUserPermission.setOnClickListener {
        viewModel.changeSwitchUserDialog()
      }
      allowedAttempts.setOnClickListener {
        viewModel.editMaxPasswordAttemptsDialog()
      }
      destroyData.setOnClickListener {
        viewModel.destroyDataDialog()
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
        val serviceWorking = viewModel.settingsState.value.serviceWorking
        val rootOrDhizuku = it.isRoot || it.isOwner
        with(binding) {
          runTrimItem.setSwitchEnabled(it.isRoot)
          wipeItem.setSwitchEnabled(
            rootOrDhizuku || it.isAdmin && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
          )
          disableMultiuserUi.isClickable = rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && serviceWorking
          moveToBfu.setSwitchEnabled(rootOrDhizuku && serviceWorking)
          removeItselfItem.setSwitchEnabled(rootOrDhizuku)
          hideAppItem.setSwitchEnabled(
            rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
          )
          clearItselfItem.setSwitchEnabled(rootOrDhizuku)
          bruteforceItem.setSwitchEnabled(it.isAdmin)
          logdOnBootItem.setSwitchEnabled(it.isRoot)
          logdOnStartItem.setSwitchEnabled(it.isRoot)
          setMultiuserUi.setActive(it.isRoot)
          setUserSwitcherUi.setActive(
            it.isRoot && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
          )
          switchUserPermission.setActive(
            rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
          )
          setSafeBoot.setActive(rootOrDhizuku)
          rootItem.setCheckedProgrammatically(it.isRoot, switchRootListener)
          adminRightsItem.setCheckedProgrammatically(it.isAdmin, switchAdminListener)
          dhizukuItem.setCheckedProgrammatically(it.isOwner, switchDhizukuListener)
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

  private fun listenButtonSettings() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.buttonsSettingsState.collect {
        with(binding) {
          latency.setDigit(it.latency.toString())
          clicksNumber.setDigit(it.allowedClicks.toString())
          powerButtonItem.setCheckedProgrammatically(
            it.triggerOnButton,
            switchTriggerOnButtonListener
          )
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
          disableMultiuserUi.isClickable = rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && it.serviceWorking
          moveToBfu.setSwitchEnabled(rootOrDhizuku && it.serviceWorking)
          runOnPasswordItem.setSwitchEnabled(it.serviceWorking)
          powerButtonItem.setSwitchEnabled(it.serviceWorking)
          wipeItem.setCheckedProgrammatically(it.wipe, switchWipeListener)
          runTrimItem.setCheckedProgrammatically(it.trim, switchTrimListener)
          removeItselfItem.setCheckedProgrammatically(
            it.removeItself,
            switchSelfDestructListener
          )
          accessibilityServiceItem.setCheckedProgrammatically(
            it.serviceWorking,
            switchAccessibilityServiceListener
          )
          logdOnBootItem.setCheckedProgrammatically(
            it.stopLogdOnBoot,
            switchLogdOnBootListener
          )
          logdOnStartItem.setCheckedProgrammatically(
            it.stopLogdOnStart,
            switchLogdOnStartListener
          )
          hideAppItem.setCheckedProgrammatically(it.hideItself, switchHideListener)
          clearItselfItem.setCheckedProgrammatically(it.clearItself, switchClearListener)
          allowScreenshots.setCheckedProgrammatically(it.uiSettings.allowScreenshots, switchScreenshotsListener)
          clearDataItem.setCheckedProgrammatically(it.clearData, switchClearDataListener)
          runOnPasswordItem.setCheckedProgrammatically(
            it.runOnDuressPassword,
            switchRunOnDuressPasswordListener
          )
          val text = when (it.uiSettings.theme) {
            Theme.SYSTEM_THEME -> requireContext().getString(R.string.system_theme)
            Theme.DARK_THEME -> requireContext().getString(R.string.dark_theme)
            Theme.LIGHT_THEME -> requireContext().getString(R.string.light_theme)
          }
          themeMenu.setText(text)
          usbMenu.isClickable = it.serviceWorking
        }
      }
      }
  }


  private fun setupThemesMenu() {
    binding.themeMenu.setOnClickListener {
      showThemesMenu()
    }
  }

  private fun openDhizukuLink() {
    val browserIntent = Intent(Intent.ACTION_VIEW, "https://github.com/iamr0s/Dhizuku".toUri())
    startActivity(browserIntent)
  }

  private fun createProfile() {
    try {
      startActivity(Intent("android.settings.USER_SETTINGS"))
    } catch (_: ActivityNotFoundException) {
    }
  }

  private fun listenQuestionDialog(tag: String, function: () -> Unit) {
    com.sonozaki.dialogs.QuestionDialog.setupListener(
      parentFragmentManager,
      tag,
      viewLifecycleOwner
    ) {
      function()
    }
  }

  /**
   * Listening for dialog result
   */
  private fun listenDialogResults() {
    listenQuestionDialog(
      MOVE_TO_ACCESSIBILITY_SERVICE,
    ) {
      startAccessibilityService()
    }
    listenQuestionDialog(MOVE_TO_BFU_DIALOG) {
      viewModel.setMoveToBFU(true)
    }
    listenQuestionDialog(DISABLE_MULTIUSER_UI_ON_BOOT) {
      viewModel.setMultiUserUIProtection(MultiuserUIProtection.ON_REBOOT)
    }
    listenQuestionDialog(DISABLE_MULTIUSER_UI_ON_LOCK) {
      viewModel.setMultiUserUIProtection(MultiuserUIProtection.ON_SCREEN_OFF)
    }
    listenQuestionDialog(
      INSTALL_DIZUKU_DIALOG,
    ) {
      openDhizukuLink()
    }
    listenQuestionDialog(
      MOVE_TO_ADMIN_SETTINGS,
    ) {
      requestAdminRights()
    }
    listenQuestionDialog(
      TRIM_DIALOG,
    ) {
      viewModel.setRunTRIM(true)
    }
    listenQuestionDialog(
      WIPE_DIALOG
    ) {
      viewModel.setWipe(true)
    }
    listenQuestionDialog(
      SELF_DESTRUCTION_DIALOG,
    ) {
      viewModel.setRemoveItself(true)
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
    listenQuestionDialog(
      HIDE_DIALOG,
    ) {
      viewModel.setHide(true)
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
    listenQuestionDialog(
      OPEN_MULTIUSER_SETTINGS_DIALOG,
    ) {
      createProfile()
    }
    listenQuestionDialog(
      RUN_ON_PASSWORD_DIALOG,
    ) {
      viewModel.setRunOnDuressPassword(true)
    }
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
    listenQuestionDialog(
      CLEAR_DIALOG
    ) {
      viewModel.setClear(true)
    }
    listenQuestionDialog(
      CLEAR_DATA_DIALOG
    ) {
      viewModel.setClearData(true)
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
      EDIT_CLICK_NUMBER_DIALOG
    ) {
        clicks ->
      viewModel.setClicksNumber(clicks)
    }
    listenQuestionDialog(
      TRIGGER_ON_BUTTON_DIALOG
    ) { viewModel.setTriggerOnButton(true) }
    listenQuestionDialog(
      REBOOT_ON_USB_DIALOG
    ) {
      viewModel.setRebootOnUSB()
    }
    listenQuestionDialog(
      ALLOW_SCREENSHOTS_DIALOG
    ) {
      viewModel.setScreenShotsStatus(true)
    }
    listenQuestionDialog(DESTROY_DATA_DIALOG
    ) {
      router.startService(requireContext())
    }
    listenQuestionDialog(
      ROOT_WARNING_DIALOG
    ) {
      viewModel.askRoot()
    }
  }

  /**
   * Setting up dialog launcher
   */
  private fun setupDialogs() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.settingsActionsFlow.collect {
        when(it) {
          is SettingsAction.ShowDialog -> dialogLauncher.launchDialogFromAction(it.value)
          is SettingsAction.ShowFaq -> findNavController().navigate(R.id.action_settingsFragment_to_aboutSettingsFragment)
        }
      }
    }
  }

  private fun setupActivity() {
    val activity = requireActivity()
    if (activity is ActivityStateHolder)
      activity.setActivityState(
        ActivityState.NormalActivityState(
          getString(R.string.settings)
        )
      )
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


  /**
   * Changing app's theme
   */
  private fun showThemesMenu() {
    val popup = PopupMenu(context, binding.themeMenu.menu)
    popup.menuInflater.inflate(R.menu.themes_menu, popup.menu)
    popup.setOnMenuItemClickListener {
      val theme = when (it.itemId) {
        R.id.dark_theme -> {
          Theme.DARK_THEME
        }

        R.id.light_theme -> {
          Theme.LIGHT_THEME
        }

        R.id.system_theme -> {
          Theme.SYSTEM_THEME
        }

        else -> throw RuntimeException("Wrong priority in priority sorting")
      }
      viewModel.setTheme(theme)
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

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}
