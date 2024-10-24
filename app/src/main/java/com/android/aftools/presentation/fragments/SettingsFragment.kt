package com.android.aftools.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CLEAR_DATA_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.CLEAR_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_MULTIUSER_UI_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SAFE_BOOT_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_SWITCH_USER_RESTRICTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.DISABLE_USER_SWITCHER_DIALOG
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
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.RUN_ON_PASSWORD_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.SELF_DESTRUCTION_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.TRIM_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.USB_DIALOG
import com.android.aftools.presentation.viewmodels.SettingsVM.Companion.WIPE_DIALOG
import com.google.android.material.color.MaterialColors
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
   * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
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

    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.settingsState.collect {
        binding.switchRunOnPassword.isEnabled = it.serviceWorking
        binding.switchUsbConnection.isEnabled = it.serviceWorking
        binding.switchWipe.setCheckedProgrammatically(it.wipe,switchWipeListener)
        binding.switchTrim.setCheckedProgrammatically(it.trim, switchTrimListener)
        binding.switchSelfDestruct.setCheckedProgrammatically(it.removeItself, switchSelfDestructListener)
        binding.switchAccessibility.setCheckedProgrammatically(it.serviceWorking,switchAccessibilityServiceListener)
        binding.switchLogdOnBoot.setCheckedProgrammatically(it.stopLogdOnBoot, switchLogdOnBootListener)
        binding.switchLogdOnStart.setCheckedProgrammatically(it.stopLogdOnStart, switchLogdOnStartListener)
        binding.switchHide.setCheckedProgrammatically(it.hideItself, switchHideListener)
        binding.switchClear.setCheckedProgrammatically(it.clearItself, switchClearListener)
        binding.switchClearData.setCheckedProgrammatically(it.clearData, switchClearDataListener)
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
        val rootOrDhizuku = it.isRoot || it.isOwner
        binding.switchTrim.isEnabled = it.isRoot
        binding.switchWipe.isEnabled = rootOrDhizuku || it.isAdmin && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        binding.switchSelfDestruct.isEnabled = rootOrDhizuku
        binding.switchHide.isEnabled = rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        binding.switchClear.isEnabled = rootOrDhizuku
        binding.switchBruteforce.isEnabled = it.isAdmin
        binding.switchLogdOnBoot.isEnabled = it.isRoot
        binding.switchLogdOnStart.isEnabled = it.isRoot
        binding.setMultiuserUi.isClickable = it.isRoot
        binding.setUserSwitcherUi.isClickable = it.isRoot && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        binding.switchUserPermission.isClickable = rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        binding.setSafeBoot.isClickable = rootOrDhizuku
        binding.multiuserUiIcon.iconTint = if (it.isRoot) {
          val color = MaterialColors.getColor(binding.multiuserUiIcon, com.google.android.material.R.attr.colorPrimary)
          ColorStateList.valueOf(color)
        } else ColorStateList.valueOf(Color.GRAY)
        binding.userSwitcherUiIcon.iconTint = if (it.isRoot && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          val color = MaterialColors.getColor(binding.userSwitcherUiIcon, com.google.android.material.R.attr.colorPrimary)
          ColorStateList.valueOf(color)
        } else ColorStateList.valueOf(Color.GRAY)
        binding.switchUserPermissionIcon.iconTint = if (rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val color = MaterialColors.getColor(binding.switchUserPermissionIcon, com.google.android.material.R.attr.colorPrimary)
          ColorStateList.valueOf(color)
        } else ColorStateList.valueOf(Color.GRAY)
        binding.safeBootIcon.iconTint = if (rootOrDhizuku) {
          val color = MaterialColors.getColor(binding.safeBootIcon, com.google.android.material.R.attr.colorPrimary)
          ColorStateList.valueOf(color)
        } else ColorStateList.valueOf(Color.GRAY)
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
      binding.switchHide.setOnCheckedChangeListener(switchHideListener)
      binding.switchClear.setOnCheckedChangeListener(switchClearListener)
      binding.switchClearData.setOnCheckedChangeListener(switchClearDataListener)
      binding.switchRunOnPassword.setOnCheckedChangeListener(switchRunOnDuressPasswordListener)
      viewModel.switchesInitialized = true
    }
    binding.setMultiuserUi.setOnClickListener {
      viewModel.changeMultiuserUISettingsDialog()
    }
    binding.switchClear.setOnClickListener {

    }
    binding.switchClearData.setOnClickListener {

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
      USB_DIALOG,
    ) {
      viewModel.setUsbConnectionStatus(true)
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
