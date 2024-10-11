package com.android.aftools.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SettingsFragmentBinding
import com.android.aftools.domain.entities.Theme
import com.android.aftools.presentation.activities.MainActivity
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.InputDigitDialog
import com.android.aftools.presentation.dialogs.PasswordInputDialog
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.viewmodels.SettingsVM
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.BRUTEFORCE_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CHANGE_USER_LIMIT_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CLEAR_AND_HIDE_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_MULTIUSER_UI_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_USER_SWITCHER_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_MULTIUSER_UI_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.ENABLE_USER_SWITCHER_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.INSTALL_DIZUKU_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.LOGD_ON_BOOT_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.LOGD_ON_START_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MAX_PASSWORD_ATTEMPTS_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MOVE_TO_ACCESSIBILITY_SERVICE
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.MOVE_TO_ADMIN_SETTINGS
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.OPEN_MULTIUSER_SETTINGS_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.RUN_ON_PASSWORD_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.SELF_DESTRUCTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.TRIM_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.USB_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.WIPE_DIALOG
import com.google.android.material.materialswitch.MaterialSwitch
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
    listenDialogResults()
    setupDialogs()
    setupMenu()
    setupButtonsAndSwitches()
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

  /**
   * Function for setting switch state without triggering OnCheckedChangeListener
   */
  private fun MaterialSwitch.setCheckedProgrammatically(value: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
      setOnCheckedChangeListener(null)
      isChecked = value
      setOnCheckedChangeListener(listener)
  }

  /**
   * Setting up buttons and switches
   */
  private fun setupButtonsAndSwitches() {

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
      viewModel.askRoot()
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

    val switchUsbListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
      if (!checked) {
        viewModel.setUsbConnectionStatus(false)
        return@OnCheckedChangeListener
      }
      switch.isChecked = false
      viewModel.showRunOnUSBConnectionDialog()
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

    val switchClearAndHideListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
      if (!checked) {
        viewModel.setClearAndHide(false)
        return@OnCheckedChangeListener
      }
      switch.isChecked = false
      viewModel.showClearAndHideDialog()
    }

    val switchRunOnDuressPasswordListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
      if (!checked) {
        viewModel.setRunOnDuressPassword(false)
        return@OnCheckedChangeListener
      }
      switch.isChecked = false
      viewModel.showRunOnDuressPasswordDialog()
    }

    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.settingsState.collect {
        binding.switchWipe.setCheckedProgrammatically(it.wipe,switchWipeListener)
        binding.switchTrim.setCheckedProgrammatically(it.trim, switchTrimListener)
        binding.switchSelfDestruct.setCheckedProgrammatically(it.removeItself, switchSelfDestructListener)
        binding.switchAccessibility.setCheckedProgrammatically(it.serviceWorking,switchAccessibilityServiceListener)
        binding.switchLogdOnBoot.setCheckedProgrammatically(it.stopLogdOnBoot, switchLogdOnBootListener)
        binding.switchLogdOnStart.setCheckedProgrammatically(it.stopLogdOnStart, switchLogdOnStartListener)
        binding.switchClearAndHide.setCheckedProgrammatically(it.clearAndHideItself, switchClearAndHideListener)
        binding.switchRunOnPassword.setCheckedProgrammatically(it.runOnDuressPassword, switchRunOnDuressPasswordListener)
        binding.showMenu.text = when(it.theme) {
          Theme.SYSTEM_THEME -> requireContext().getString(R.string.system_theme)
          Theme.DARK_THEME -> requireContext().getString(R.string.dark_theme)
          Theme.LIGHT_THEME -> requireContext().getString(R.string.light_theme)
        }
      }
    }
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.bruteforceProtectionState.collect {
        binding.switchBruteforce.setCheckedProgrammatically(it.bruteforceRestricted,switchBruteforceListener)
      }
    }
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.permissionsState.collect {
        binding.switchRoot.setCheckedProgrammatically(it.isRoot,switchRootListener)
        binding.switchAdmin.setCheckedProgrammatically(it.isAdmin,switchAdminListener)
        binding.switchDhizuku.setCheckedProgrammatically(it.isOwner,switchDhizukuListener)
      }
    }
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.usbSettingState.collect {
        binding.switchUsbConnection.setCheckedProgrammatically(it.runOnConnection,switchUsbListener)
      }
    }
    binding.setupPassword.setOnClickListener {
      viewModel.showPasswordInput()
    }
    binding.usersLimit.setOnClickListener {
      viewModel.showChangeUserLimitDialog()
    }
    if (!viewModel.switchesInitialized) {
      binding.switchAccessibility.setOnCheckedChangeListener(switchAccessibilityServiceListener)
      binding.switchAdmin.setOnCheckedChangeListener(switchAdminListener)
      binding.switchDhizuku.setOnCheckedChangeListener(switchDhizukuListener)
      binding.switchRoot.setOnCheckedChangeListener(switchRootListener)
      binding.switchTrim.setOnCheckedChangeListener(switchTrimListener)
      binding.switchWipe.setOnCheckedChangeListener(switchWipeListener)
      binding.switchSelfDestruct.setOnCheckedChangeListener(switchSelfDestructListener)
      binding.switchUsbConnection.setOnCheckedChangeListener(switchUsbListener)
      binding.switchBruteforce.setOnCheckedChangeListener(switchBruteforceListener)
      binding.switchLogdOnBoot.setOnCheckedChangeListener(switchLogdOnBootListener)
      binding.switchLogdOnStart.setOnCheckedChangeListener(switchLogdOnStartListener)
      binding.switchClearAndHide.setOnCheckedChangeListener(switchClearAndHideListener)
      binding.switchRunOnPassword.setOnCheckedChangeListener(switchRunOnDuressPasswordListener)
      viewModel.switchesInitialized = true
    }
    binding.setMultiuserUi.setOnClickListener {
      viewModel.changeMultiuserUISettingsDialog()
    }
    binding.setSafeBoot.setOnClickListener {
      viewModel.changeSafeBootRestrictionSettingsDialog()
    }
    binding.setUserSwitcherUi.setOnClickListener {
      viewModel.changeUserSwitcherDialog()
    }
    binding.switchUserPermission.setOnClickListener {
      viewModel.changeSwitchUserDialog()
    }
    binding.allowedAttempts.setOnClickListener {
      viewModel.editMaxPasswordAttemptsDialog()
    }
  }


  private fun setupThemesMenu() {
    binding.showMenu.setOnClickListener {
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
    QuestionDialog.setupListener(
      parentFragmentManager,
      MOVE_TO_ACCESSIBILITY_SERVICE,
      viewLifecycleOwner
    ) {
      startAccessibilityService()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      INSTALL_DIZUKU_DIALOG,
      viewLifecycleOwner
    ) {
      openDhizukuLink()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      MOVE_TO_ADMIN_SETTINGS,
      viewLifecycleOwner
    ) {
      requestAdminRights()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      TRIM_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setRunTRIM(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      WIPE_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setWipe(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      SELF_DESTRUCTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setRemoveItself(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      SELF_DESTRUCTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setRemoveItself(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      USB_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setUsbConnectionStatus(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      BRUTEFORCE_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setBruteforceProtection(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      LOGD_ON_START_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setLogdOnStartStatus(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      LOGD_ON_BOOT_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setLogdOnBootStatus(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      CLEAR_AND_HIDE_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setClearAndHide(true)
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
    QuestionDialog.setupListener(
      parentFragmentManager,
      OPEN_MULTIUSER_SETTINGS_DIALOG,
      viewLifecycleOwner
    ) {
      createProfile()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      RUN_ON_PASSWORD_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setRunOnDuressPassword(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      ENABLE_SAFE_BOOT_RESTRICTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setSafeBoot(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      DISABLE_SAFE_BOOT_RESTRICTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setSafeBoot(false)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      ENABLE_MULTIUSER_UI_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setMultiuserUI(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      DISABLE_MULTIUSER_UI_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setMultiuserUI(false)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      ENABLE_USER_SWITCHER_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setUserSwitcher(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      DISABLE_USER_SWITCHER_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setUserSwitcher(false)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      ENABLE_SWITCH_USER_RESTRICTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setUserSwitchRestriction(true)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      DISABLE_SWITCH_USER_RESTRICTION_DIALOG,
      viewLifecycleOwner
    ) {
      viewModel.setUserSwitchRestriction(false)
    }
  }

  /**
   * Setting up dialog launcher
   */
  private fun setupDialogs() {
    val dialogLauncher = DialogLauncher(parentFragmentManager, context)
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.settingsActionsFlow.collect {
        dialogLauncher.launchDialogFromAction(it)
      }
    }
  }

  private fun setupActivity() {
    (activity as MainActivity).setActivityState(
      ActivityState.NormalActivityState(
        getString(R.string.settings)
      )
    )
  }

  /**
   * Changing app's theme
   */
  private fun showThemesMenu() {
    val popup = PopupMenu(context, binding.showMenu)
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

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}
