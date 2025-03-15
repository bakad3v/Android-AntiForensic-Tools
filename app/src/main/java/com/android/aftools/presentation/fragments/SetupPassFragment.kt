package com.android.aftools.presentation.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_SEND
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SetupPassFragmentBinding
import com.android.aftools.passwordStrength.PasswordStrength
import com.android.aftools.passwordStrength.PasswordStrengthData
import com.android.aftools.presentation.activities.ActivityStateHolder
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.SetupPasswordState
import com.android.aftools.presentation.viewmodels.SetupPasswordVM
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupPassFragment : Fragment() {
    private val viewModel: SetupPasswordVM by viewModels()
    private var _binding: SetupPassFragmentBinding? = null
    private val controller by lazy { findNavController() }
    private val args: SetupPassFragmentArgs by navArgs()
    private val binding
        get() = _binding ?: throw RuntimeException("MainFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            SetupPassFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.passwordHint.text = Html.fromHtml(requireContext().getString(R.string.duress_password), Html.FROM_HTML_MODE_LEGACY)
        binding.password.imeOptions = IME_FLAG_NO_PERSONALIZED_LEARNING + IME_ACTION_SEND
        setupActivityAndText()
        observePasswordEntered()
        observePasswordCreated()
        observeState()
    }

    private fun setupActivityAndText() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            if (args.fromSplash) {
                activity.setActivityState(ActivityState.NoActionBarNoDrawerActivityState)
            } else {
                binding.setupPassword.visibility = View.GONE
                activity.setActivityState(
                    ActivityState.NormalActivityState(
                        requireContext().getString(
                            R.string.change_password
                        )
                    )
                )
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordState.collect {
                displayPasswordStrength(it.passwordStrength)
                when (it) {
                    is SetupPasswordState.CheckEnterPasswordResults -> {
                        if (it.validateResult.isSuccess) {
                            handlePasswordCreation()
                        } else {
                            displayError(it.validateResult.message)
                        }
                    }
                    is SetupPasswordState.PasswordStrengthCheck -> {
                        displayError(null)
                    }
                }
            }
        }
    }

    private fun displayError(message: Int?) {
        binding.passwordField.isErrorEnabled = message != null
        binding.passwordField.error = message?.let { requireContext().getString(it) }
    }

    private fun handlePasswordCreation() {
        if (args.fromSplash) {
            hideKeyboard()
            controller.navigate(R.id.action_setupPassFragment_to_settingsFragment)
        } else {
            Snackbar.make(binding.root,R.string.password_changed, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun displayPasswordStrength(passwordStrength: PasswordStrengthData) {
        setupStrengthText(passwordStrength)
        setupStrengthIndicator(passwordStrength)
    }

    private fun setupStrengthIndicator(passwordStrength: PasswordStrengthData) {
        binding.strengthIndicator.setTrackColor(passwordStrength.strength.color)
        binding.strengthIndicator.setTracksProgress(passwordStrength.strength.ordinal)
    }

    private fun setupStrengthText(passwordStrengthData: PasswordStrengthData) {
        with(binding) {
            if (passwordStrengthData.strength == PasswordStrength.EMPTY) {
                passwordStrength.text = requireContext().getString(R.string.empty_password)
                timeToCrackOffline.text =
                    requireContext().getString(R.string.crack_time_undefined)
                timeToCrackOnline.text =
                    requireContext().getString(R.string.crack_time_undefined)
                return
            }
            val strengthText = requireContext().getString(passwordStrengthData.strength.commentary)
            passwordStrength.text =
                requireContext().getString(R.string.password_strength, strengthText)
            timeToCrackOnline.text = requireContext().getString(R.string.crack_time_online, passwordStrengthData.timeToCrackOnline)
            timeToCrackOffline.text = requireContext().getString(R.string.crack_time_offline, passwordStrengthData.timeToCrackOffline)
        }
    }

    private fun observePasswordEntered() {
        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.passwordChanged(text.toString())
        }
    }

    private fun observePasswordCreated() {
        binding.password.setOnEditorActionListener { textView, actionId, _ ->
            var handled = false
            if (actionId == IME_ACTION_SEND) {
                viewModel.createPassword(textView.text.toList().toCharArray())
                handled = true
            }
            return@setOnEditorActionListener handled
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}