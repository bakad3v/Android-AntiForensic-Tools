package com.sonozaki.passwordsetup.presentation.fragment

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
import com.google.android.material.snackbar.Snackbar
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.passwordsetup.R
import com.sonozaki.passwordsetup.databinding.SetupPassFragmentBinding
import com.sonozaki.passwordsetup.domain.router.PasswordSetupRouter
import com.sonozaki.passwordsetup.presentation.states.SetupPasswordState
import com.sonozaki.passwordsetup.presentation.viewmodel.SetupPasswordVM
import com.sonozaki.passwordstrength.PasswordStrength
import com.sonozaki.passwordstrength.PasswordStrengthData
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupPassFragment : Fragment() {
    private val viewModel: SetupPasswordVM by viewModels()
    private var _binding: SetupPassFragmentBinding? = null
    private val controller by lazy { findNavController() }
    private val binding
        get() = _binding ?: throw RuntimeException("MainFragmentBinding == null")

    @Inject
    lateinit var router: PasswordSetupRouter

    //was this screen opened for password creation or for password altering?
    private val fromSplash by lazy { router.getFromSplash(this) }

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
        binding.passwordHint.text = Html.fromHtml(
            requireContext().getString(R.string.duress_password),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.password.imeOptions = IME_FLAG_NO_PERSONALIZED_LEARNING + IME_ACTION_SEND
        setupActivityAndText()
        observePasswordEntered()
        observePasswordCreated()
        observeState()
        awaitPasswordCreation()
    }

    /**
     * Wait for user to create new password
     */
    private fun awaitPasswordCreation() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordCreatedFlow.collect {
                handlePasswordCreation()
            }
        }
    }

    private fun setupActivityAndText() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            if (fromSplash) {
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

    /**
     * Change UI to display password entering state.
     */
    private fun observeState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordState.collect {
                displayPasswordStrength(it.passwordStrength)
                when (it) {
                    is SetupPasswordState.DisplayError -> {
                        displayError(it.errorMessage)
                    }

                    is SetupPasswordState.PasswordStrengthCheck -> {
                        displayError(null)
                    }
                }
            }
        }
    }

    /**
     * Display error when entered incorrect password
     */
    private fun displayError(message: Int?) {
        binding.passwordField.isErrorEnabled = message != null
        binding.passwordField.error = message?.let { requireContext().getString(it) }
    }

    /**
     * Hide keyboard and move to next screen if password was created successfully
     */
    private fun handlePasswordCreation() {
        if (fromSplash) {
            hideKeyboard()
            router.openNextScreen(controller)
        } else {
            Snackbar.make(binding.root, R.string.password_changed, Snackbar.LENGTH_SHORT).show()
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
            timeToCrackOnline.text = requireContext().getString(
                R.string.crack_time_online,
                passwordStrengthData.timeToCrackOnline
            )
            timeToCrackOffline.text = requireContext().getString(
                R.string.crack_time_offline,
                passwordStrengthData.timeToCrackOffline
            )
        }
    }


    private fun observePasswordEntered() {
        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.passwordChanged(text.toString())
        }
    }

    private fun observePasswordCreated() {
        binding.password.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == IME_ACTION_SEND) {
                viewModel.createPassword(textView.text.toList().toCharArray())
            }
            return@setOnEditorActionListener false
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