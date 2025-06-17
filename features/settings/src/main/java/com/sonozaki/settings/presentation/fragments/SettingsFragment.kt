package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("SettingsFragmentBinding == null")
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity()
        setupMenu()
        listenClickable()
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
                    R.id.help -> navController.navigate(R.id.action_settingsFragment_to_aboutSettingsFragment)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun listenClickable() {
        with(binding) {
            dataDestructionSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_dataDestructionSettingsFragment)
            }
            multiuserSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_multiuserSettingsFragment)
            }
            permanentSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_permanentSettingsFragment)
            }
            permissionsSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_permissionSettingsFragment)
            }
            triggersSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_triggerSettingsFragment)
            }
            uiSettings.setOnClickListener {
                navController.navigate(R.id.action_settingsFragment_to_UISettingsFragment)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}