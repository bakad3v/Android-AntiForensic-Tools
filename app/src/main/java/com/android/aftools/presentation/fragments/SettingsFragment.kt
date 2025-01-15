package com.android.aftools.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SettingsFragmentBinding
import com.android.aftools.domain.entities.Theme
import com.android.aftools.domain.entities.UsbSettings
import com.android.aftools.presentation.actions.SettingsAction
import com.android.aftools.presentation.activities.ActivityStateHolder
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.InputDigitDialog
import com.android.aftools.presentation.dialogs.PasswordInputDialog
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.viewmodels.SettingsVM
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.BRUTEFORCE_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CHANGE_USER_LIMIT_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CLEAR_DATA_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CLEAR_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_MULTIUSER_UI_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_USER_SWITCHER_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.EDIT_CLICK_NUMBER_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.EDIT_LATENCY_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_MULTIUSER_UI_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_USER_SWITCHER_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.HIDE_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.INSTALL_DIZUKU_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.LOGD_ON_BOOT_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.LOGD_ON_START_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MAX_PASSWORD_ATTEMPTS_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MOVE_TO_ACCESSIBILITY_SERVICE
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MOVE_TO_ADMIN_SETTINGS
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.OPEN_MULTIUSER_SETTINGS_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.REBOOT_ON_USB_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ROOT_WARNING_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.RUN_ON_PASSWORD_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.RUN_ON_USB_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.SELF_DESTRUCTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.TRIGGER_ON_BUTTON_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.TRIM_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.WIPE_DIALOG
import dagger.hilt.android.AndroidEntryPoint


/**
 * Fragment for managing app settings
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

  private val viewModel: SettingsVM by viewModels()
  private var _binding: SettingsFragmentBinding? = null
  private val binding
    get() = _binding ?: throw RuntimeException("SettingsFragmentBinding == null")

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
    listenDialogResults()
    setupDialogs()
    setupMenu()
    setupButtonsAndSwitches()
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
  val switchWipeListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setWipe(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showWipeDialog()
  }

  val switchTrimListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRunTRIM(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showTRIMDialog()
  }

  val switchBruteforceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setBruteforceProtection(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showBruteforceDialog()
  }

  val switchSelfDestructListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRemoveItself(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showSelfDestructionDialog()
  }

  val switchRootListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.showRootDisableDialog()
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showRootWarningDialog()
  }

  val switchAdminListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.disableAdmin()
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    requestAdminRights()
  }

  val switchAccessibilityServiceListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    switch.isChecked = !checked
    viewModel.showAccessibilityServiceDialog()
  }

  val switchDhizukuListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    switch.isChecked = !checked
    viewModel.askDhizuku()
  }

  val switchLogdOnStartListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setLogdOnStartStatus(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showLogdOnStartDialog()
  }

  val switchLogdOnBootListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setLogdOnBootStatus(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showLogdOnBootDialog()
  }

  val switchHideListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setHide(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showHideDialog()
  }

  val switchClearListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setClear(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showClearDialog()
  }

  val switchClearDataListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setClearData(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showClearDataDialog()
  }

  val switchRunOnDuressPasswordListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setRunOnDuressPassword(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showRunOnDuressPasswordDialog()
  }

  val switchTriggerOnButtonListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
    if (!checked) {
      viewModel.setTriggerOnButton(false)
      return@OnCheckedChangeListener
    }
    switch.isChecked = false
    viewModel.showSetTriggerOnButtonDialog()
  }

  /**
   * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
   */
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
      setupPassword.setOnClickListener {
        viewModel.showPasswordInput()
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
        val rootOrDhizuku = it.isRoot || it.isOwner
        with(binding) {
          runTrimItem.setSwitchEnabled(it.isRoot)
          wipeItem.setSwitchEnabled(
            rootOrDhizuku || it.isAdmin && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
          )
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
        with(binding) {
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
          clearDataItem.setCheckedProgrammatically(it.clearData, switchClearDataListener)
          runOnPasswordItem.setCheckedProgrammatically(
            it.runOnDuressPassword,
            switchRunOnDuressPasswordListener
          )
          val text = when (it.theme) {
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
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/iamr0s/Dhizuku"))
    startActivity(browserIntent)
  }

  private fun createProfile() {
    try {
      startActivity(Intent("android.settings.USER_SETTINGS"))
    } catch (e: ActivityNotFoundException) {
    }
  }

  private fun listenQuestionDialog(tag: String, function: () -> Unit) {
    QuestionDialog.setupListener(
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
    PasswordInputDialog.setupListener(
      parentFragmentManager,
      viewLifecycleOwner
    ) {
      viewModel.setPassword(it)
    }
    listenQuestionDialog(
      MOVE_TO_ACCESSIBILITY_SERVICE,
    ) {
      startAccessibilityService()
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
      ROOT_WARNING_DIALOG
    ) {
      viewModel.askRoot()
    }
  }

  /**
   * Setting up dialog launcher
   */
  private fun setupDialogs() {
    val dialogLauncher = DialogLauncher(parentFragmentManager, context)
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
