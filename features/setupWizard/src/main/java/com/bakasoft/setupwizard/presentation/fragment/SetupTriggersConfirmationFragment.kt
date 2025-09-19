package com.bakasoft.setupwizard.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bakasoft.setupwizard.R
import com.bakasoft.setupwizard.databinding.ConfirmationFragmentBinding
import com.bakasoft.setupwizard.domain.entities.AvailableTriggers
import com.bakasoft.setupwizard.presentation.actions.ConfirmationFragmentsActions
import com.bakasoft.setupwizard.presentation.states.TriggersConfirmationState
import com.bakasoft.setupwizard.presentation.viewmodel.SetupTriggersConfirmationVM
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SetupTriggersConfirmationFragment: Fragment() {
    private val binding: ConfirmationFragmentBinding get() = _binding?: throw RuntimeException("DialogFragmentBinding == null")
    private var _binding: ConfirmationFragmentBinding? = null
    private val viewModel: SetupTriggersConfirmationVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            ConfirmationFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        listenState()
        listenActions()
        setMainActivityState()
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            activity.setActivityState(ActivityState.NormalActivityState(requireContext().getString(R.string.apply_settings)))
        }
    }

    private fun listenActions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.actionsFlow.collect {
                when(it) {
                    ConfirmationFragmentsActions.NAVIGATE_BACK -> {
                        findNavController().navigate(R.id.action_setupTriggersConfirmationFragment_to_setupWizardFragment)
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        with(binding) {
            displayLoading.isVisible = loading
            dataLayout.isVisible = !loading
        }
    }

    private fun listenState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.availableTriggersState.collect {
                when(it) {
                    is TriggersConfirmationState.Loading -> showLoading(true)
                    is TriggersConfirmationState.Data -> {
                        showLoading(false)
                        displayText(it.availableTriggers)
                    }
                }
            }
        }
    }

    private fun displayText(triggers: AvailableTriggers) {
        with(binding) {
            text.text = HtmlCompat.fromHtml(
                when(triggers) {
                    is AvailableTriggers.NoTriggers -> requireContext().getString(R.string.cant_setup_triggers)
                    is AvailableTriggers.VolumeButton -> requireContext().getString(R.string.setup_volume_button, triggers.clicks, triggers.allowedAttempts)
                    is AvailableTriggers.PowerButton -> requireContext().getString(R.string.setup_power_button, triggers.clicks, triggers.allowedAttempts)
            }, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun setupButtons() {
        with(binding) {
            okBtn.setOnClickListener {
                viewModel.setupTriggers()
            }
            cancelBtn.setOnClickListener {
                viewModel.navigateBack()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}