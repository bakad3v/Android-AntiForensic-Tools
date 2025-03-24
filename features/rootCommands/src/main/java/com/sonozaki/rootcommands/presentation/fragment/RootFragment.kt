package com.sonozaki.rootcommands.presentation.fragment

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
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.dialogs.QuestionDialog
import com.sonozaki.rootcommands.R
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.sonozaki.rootcommands.databinding.RootFragmentBinding
import com.sonozaki.rootcommands.presentation.state.RootState
import com.sonozaki.rootcommands.presentation.viewmodel.RootVM
import com.sonozaki.rootcommands.presentation.viewmodel.RootVM.Companion.ENABLE_ROOT_COMMANDS_DIALOG
import com.sonozaki.rootcommands.presentation.viewmodel.RootVM.Companion.NO_SUPERUSER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.getValue

/**
 * Fragment for setting up custom root commands
 */
@AndroidEntryPoint
class RootFragment : Fragment() {
    private val viewModel: RootVM by viewModels()
    private var _binding: RootFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("RootFragment == null")
    private val dialogLauncher by lazy {
        com.sonozaki.dialogs.DialogLauncher(
            parentFragmentManager,
            context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            RootFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialogLauncher()
        setupDialogListeners()
        observeState()
        setMainActivityState()
    }

    private fun setupDialogListeners() {
        QuestionDialog.setupListener(
            parentFragmentManager,
            ENABLE_ROOT_COMMANDS_DIALOG,
            viewLifecycleOwner
        ) {
            viewModel.setRunRoot(true)
        }
        QuestionDialog.setupListener(
            parentFragmentManager,
            NO_SUPERUSER,
            viewLifecycleOwner
        ) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupDialogLauncher() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.rootActions.collect {
                dialogLauncher.launchDialogFromAction(it)
            }
        }
    }


    private fun observeState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.rootState.collect {
                with(binding) {
                    when (it) {
                        is RootState.Loading -> {
                            setupDataVisibility(false)
                            setupMenu(false)
                            setupTextInput(false)
                        }
                        RootState.ViewData -> {
                            setupDataVisibility(true)
                            setupMenu(false)
                            setupTextInput(false)
                        }
                        is RootState.NoRoot -> {
                            setupDataVisibility(true)
                            viewModel.showNoRootRightsDialog()
                            setupMenu(false)
                            setupTextInput(false)
                        }
                        is RootState.LoadCommand -> {
                            setupDataVisibility(true)
                            rootText.setText(it.commands)
                            setupMenu(false)
                            setupTextInput(false)
                        }
                        is RootState.EditData -> {
                            setupDataVisibility(true)
                            setupMenu(true)
                            setupTextInput(true)
                        }
                    }
                }
            }
        }
    }

    private fun setupDataVisibility(visible: Boolean) {
        with(binding) {
            rootTextLayout.visibility = com.sonozaki.utils.booleanToVisibility(visible)
            progressBar.visibility = com.sonozaki.utils.booleanToVisibility(!visible)
        }
    }

    private fun setupTextInput(enabled: Boolean) {
        binding.rootText.isEnabled = enabled
    }

    private suspend fun Menu.drawEnabledButton() {
        viewModel.rootCommandEnabled.collect {
            val icon: Int
            val text: Int
            if (it) {
                icon = com.sonozaki.resources.R.drawable.ic_baseline_pause_24
                text = R.string.disable_root_command
            } else {
                icon = com.sonozaki.resources.R.drawable.ic_baseline_play_arrow_24
                text = R.string.enable_root_command
            }
            withContext(Dispatchers.Main) {
                val startIcon = findItem(R.id.enable_root_command)
                    ?: throw RuntimeException("Enable root button not found")
                startIcon.setIcon(icon).setTitle(text)
            }
        }
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder)
            activity.setActivityState(
                ActivityState.NormalActivityState(
                    getString(
                        R.string.root_command_settings
                    )
                )
            )
    }

    private fun setupMenu(editing: Boolean) {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                val menuId = if (editing) {
                    R.menu.root_menu_save
                } else {
                    R.menu.root_menu_edit
                }
                viewLifecycleOwner.launchLifecycleAwareCoroutine {
                    menuInflater.inflate(menuId, menu)
                    menu.drawEnabledButton()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.enable_root_command -> viewModel.showChangeDeletionEnabledDialog()
                    R.id.save_command -> viewModel.saveRootCommand(binding.rootText.text.toString())
                    R.id.edit_command -> viewModel.edit()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}