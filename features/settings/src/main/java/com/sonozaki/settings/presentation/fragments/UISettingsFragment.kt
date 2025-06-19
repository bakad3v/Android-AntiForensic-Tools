package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.sonozaki.entities.Theme
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.UiSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.UISettingsVM
import com.sonozaki.settings.presentation.viewmodel.UISettingsVM.Companion.ALLOW_SCREENSHOTS_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UISettingsFragment: AbstractSettingsFragment() {
    override val viewModel: UISettingsVM by viewModels()
    private var _binding: UiSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("UiSettingsFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            UiSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.ui_settings)
        setupThemesMenu()
        listenDialogResults()
        setupButtonsAndSwitches()
    }

    private fun listenDialogResults() {
        listenQuestionDialog(
            ALLOW_SCREENSHOTS_DIALOG
        ) {
            viewModel.setScreenShotsStatus(true)
        }
    }

    private fun setupThemesMenu() {
        binding.themeMenu.setOnClickListener {
            showThemesMenu()
        }
    }

    private val switchScreenshotsListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setScreenShotsStatus(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showAllowScreenShotsDialog()
    }

    /**
     * Setting up buttons and switches. Switches are disabled if user doesn't provide enough rights.
     */
    private fun setupButtonsAndSwitches() {
        listenSettings()
    }

    private fun listenSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsState.collect {
                with(binding) {
                    allowScreenshots.setCheckedProgrammatically(it.uiSettings.allowScreenshots, switchScreenshotsListener)
                    val text = when (it.uiSettings.theme) {
                        Theme.SYSTEM_THEME -> requireContext().getString(R.string.system_theme)
                        Theme.DARK_THEME -> requireContext().getString(R.string.dark_theme)
                        Theme.LIGHT_THEME -> requireContext().getString(R.string.light_theme)
                    }
                    themeMenu.setText(text)
                }
            }
        }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}