package com.bakasoft.setupwizard.presentation.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
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
import com.bakasoft.setupwizard.domain.entities.AvailableProtection
import com.bakasoft.setupwizard.presentation.actions.ConfirmationFragmentsActions
import com.bakasoft.setupwizard.presentation.states.ProtectionConfirmationState
import com.bakasoft.setupwizard.presentation.viewmodel.SetupProtectionConfirmationVM
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupProtectionConfirmationFragment: Fragment() {
    private val binding: ConfirmationFragmentBinding get() = _binding?: throw RuntimeException("DialogFragmentBinding == null")
    private var _binding: ConfirmationFragmentBinding? = null
    private val viewModel: SetupProtectionConfirmationVM by viewModels()

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
                        findNavController().navigate(R.id.action_setupProtectionConfirmationFragment_to_setupWizardFragment)
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
            viewModel.availableProtectionState.collect {
                when(it) {
                    is ProtectionConfirmationState.Loading -> showLoading(true)
                    is ProtectionConfirmationState.Data -> {
                        showLoading(false)
                        displayText(it.availableProtection)
                    }
                }
            }
        }
    }

    private fun displayText(protection: AvailableProtection) {
        with(binding) {
            text.movementMethod = LinkMovementMethod.getInstance()
            text.text = HtmlCompat.fromHtml(buildString {
                appendLine(requireContext().getString(R.string.do_ypu_want_this_app_to))
                if (protection.uninstallItself) {
                    appendLine(requireContext().getString(R.string.destroy_itself_question))
                }
                if (protection.hideItself) {
                    appendLine(requireContext().getString(R.string.hide_itself_question))
                }
                if (protection.disableLogs) {
                    appendLine(requireContext().getString(R.string.disable_logs_question))
                }
                if (protection.disableSafeBoot) {
                    appendLine(requireContext().getString(R.string.disable_safe_boot_question))
                }
                if (protection.hideMultiuserUI) {
                    appendLine(requireContext().getString(R.string.hide_multiuser_ui_question))
                }
                if (protection.activateTrim) {
                    appendLine(requireContext().getString(R.string.run_trim_question))
                }
            }, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun setupButtons() {
        with(binding) {
            okBtn.setOnClickListener {
                viewModel.setupProtection()
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