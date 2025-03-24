package com.sonozaki.lockscreen.presentation.fragment

import android.os.Bundle
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
import com.sonozaki.lockscreen.R
import com.sonozaki.lockscreen.databinding.EnterPassFragmentBinding
import com.sonozaki.lockscreen.domain.router.LockScreenRouter
import com.sonozaki.lockscreen.presentation.state.EnterPasswordState
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for entering password
 */
@AndroidEntryPoint
class EnterPassFragment : Fragment() {
    private val viewModel: com.sonozaki.lockscreen.presentation.viewmodel.EnterPasswordVM by viewModels()
    private var _binding: EnterPassFragmentBinding? = null
    private val controller by lazy { findNavController() }
    private val binding
        get() = _binding ?: throw RuntimeException("MainFragmentBinding == null")

    @Inject
    lateinit var router: LockScreenRouter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            EnterPassFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        if (activity is com.sonozaki.activitystate.ActivityStateHolder) {
            activity.setActivityState(com.sonozaki.activitystate.ActivityState.NoActionBarNoDrawerActivityState)
        }
        binding.password.imeOptions = IME_FLAG_NO_PERSONALIZED_LEARNING + IME_ACTION_SEND
        observePasswordState()
        observeTextEntered()
        observePasswordCreation()
    }

    /**
     * Handling password entering
     */
    private fun observeTextEntered() {
        binding.password.doOnTextChanged { _, _, _, _ ->
            viewModel.setTextChangedState()
        }
    }

    /**
     * Handling password creation
     */
    private fun observePasswordCreation() {
        binding.password.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == IME_ACTION_SEND) {
                viewModel.passwordEntered(textView.text.toList().toCharArray())
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * Displaying state as user enters password
     */
    private fun observePasswordState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordStatus.collect {
                when (it) {
                    is EnterPasswordState.CheckEnterPasswordResults -> handlePasswordEntered(it.rightPassword)
                    is EnterPasswordState.EnteringText -> setupError(null)
                    is EnterPasswordState.Initial -> setupError(null)
                }
            }
        }
    }

    /**
     * Displaying error when wrong password entered
     */
    private fun setupError(errorText: String?) {
        with(binding.passwordField) {
            error = errorText
            isErrorEnabled = errorText != null
        }
    }

    /**
     * Handling attempt to unlock the app
     */
    private suspend fun handlePasswordEntered(correct: Boolean) {
        if (correct) {
            moveToNextScreen()
        } else {
            setupError(requireContext().getString(R.string.wrong_password))
        }
    }

    private suspend fun moveToNextScreen() {
        viewModel.writeToLogs(getString(R.string.app_entered))
        hideKeyboard()
        parentFragmentManager.popBackStack()
        router.openNextScreen(controller)
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
