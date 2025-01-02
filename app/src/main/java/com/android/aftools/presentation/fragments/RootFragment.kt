package com.android.aftools.presentation.fragments

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
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.RootFragmentBinding
import com.android.aftools.presentation.activities.ActivityStateHolder
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.RootState
import com.android.aftools.presentation.viewmodels.RootVM
import com.android.aftools.presentation.viewmodels.RootVM.Companion.ENABLE_ROOT_COMMANDS_DIALOG
import com.android.aftools.presentation.viewmodels.RootVM.Companion.NO_SUPERUSER
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
    private val dialogLauncher by lazy { DialogLauncher(parentFragmentManager, context) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            RootFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
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
                        is RootState.Loading, RootState.ViewData -> {
                            setupMenu(false)
                        }
                        is RootState.NoRoot -> {
                            viewModel.showNoRootRightsDialog()
                            setupMenu(false)
                        }
                        is RootState.LoadCommand -> {
                            rootText.setText(it.commands)
                            setupMenu(false)
                        }
                        is RootState.EditData -> {
                            setupMenu(true)
                        }
                    }
                }
            }
        }
    }

    private suspend fun Menu.drawEnabledButton() {
        viewModel.rootCommandEnabled.collect {
            val icon: Int
            val text: Int
            if (it) {
                icon = R.drawable.ic_baseline_pause_24
                text = R.string.disable_root_command
            } else {
                icon = R.drawable.ic_baseline_play_arrow_24
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

}