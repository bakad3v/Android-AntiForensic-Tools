package com.android.aftools.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SetupProfilesFragmentBinding
import com.android.aftools.presentation.activities.ActivityStateHolder
import com.android.aftools.presentation.adapters.profileAdapter.ProfileAdapter
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.ProfilesDataState
import com.android.aftools.presentation.viewmodels.ProfilesVM
import com.android.aftools.presentation.viewmodels.ProfilesVM.Companion.CHANGE_PROFILES_DELETION_ENABLED
import com.android.aftools.presentation.viewmodels.ProfilesVM.Companion.NO_SUPERUSER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fragment for storing profiles data
 */
@AndroidEntryPoint
class ProfilesFragment: Fragment() {
    private val viewModel: ProfilesVM by viewModels()
    private var _binding: SetupProfilesFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("ProfilesFragment == null")
    private val dialogLauncher by lazy { DialogLauncher(parentFragmentManager, context) }


    @Inject
    lateinit var myProfileAdapter: ProfileAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            SetupProfilesFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupProfilesDataListener()
        setupDialogListeners()
        setupActionsListener()
        setMainActivityState()
        setupMenu()
    }

    /**
     * Rendering button for enabling or disabling file deletion
     */
    private suspend fun Menu.drawSwitchProfileDeletionStatusButton() {
        viewModel.profileDeletionEnabled.collect {
            val icon: Int
            val text: Int
            if (it) {
                icon = R.drawable.ic_baseline_pause_24
                text = R.string.disable_profile_deletion
            } else {
                icon = R.drawable.ic_baseline_play_arrow_24
                text = R.string.enable_profile_deletion
            }
            withContext(Dispatchers.Main) {
                val startIcon = findItem(R.id.enable)
                    ?: throw RuntimeException("Enable profiles button not found")
                startIcon.setIcon(icon).setTitle(text)
            }
        }
    }

    /**
     * Setting up menu
     */
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                viewLifecycleOwner.launchLifecycleAwareCoroutine {
                    menuInflater.inflate(R.menu.profiles_menu, menu)
                    menu.drawSwitchProfileDeletionStatusButton()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.help -> viewModel.showFAQ()
                    R.id.refresh -> viewModel.refreshProfilesData()
                    R.id.enable -> viewModel.showChangeDeletionEnabledDialog()
                    R.id.add_profile -> createProfile()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createProfile() {
        try {
            startActivity(Intent("android.settings.USER_SETTINGS"))
        } catch (e: ActivityNotFoundException) {
            viewModel.showNoUserSettingsDialog()
        }
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder)
            activity.setActivityState(
                ActivityState.NormalActivityState(
                    getString(
                        R.string.profiles_deletion_settings
                    )
                )
            )
    }

    /**
    * Setting up dialog launcher
    */
    private fun setupActionsListener() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.profileActions.collect {
                dialogLauncher.launchDialogFromAction(it)
            }
        }
    }

    /**
     * Sending data to adapter
     */
    private fun setupProfilesDataListener() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.profiles.collect {
                if (it is ProfilesDataState.ViewData) {
                    myProfileAdapter.submitList(it.items)
                }
            }
        }
    }

    /**
     * Setting recyclerview buttons listeners
     */
    private fun ProfileAdapter.setRecyclerViewListeners() {
        onDeleteItemClickListener = { id, status -> viewModel.setProfileDeletionStatus(id, status) }
    }

    /**
     * Setting recyclerview
     */
    private fun setupRecyclerView() {
        with(binding.items) {
            layoutManager = LinearLayoutManager(context)
            myProfileAdapter.setRecyclerViewListeners()
            adapter = myProfileAdapter
        }
    }

    /**
     * Listening to dialogs results
     */
    private fun setupDialogListeners() {
        QuestionDialog.setupListener(
            parentFragmentManager,
            CHANGE_PROFILES_DELETION_ENABLED,
            viewLifecycleOwner
        ) {
            viewModel.changeDeletionEnabled()
        }
        QuestionDialog.setupListener(
            parentFragmentManager,
            NO_SUPERUSER,
            viewLifecycleOwner
        ) {
            parentFragmentManager.popBackStack()
        }

    }

    override fun onDestroyView() {
        binding.items.setAdapter(null)
        _binding = null
        super.onDestroyView()
    }
}