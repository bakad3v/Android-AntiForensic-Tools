package com.android.aftools.presentation.fragments

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
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.EnterPassFragmentBinding
import com.android.aftools.presentation.activities.ActivityStateHolder
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.EnterPasswordState
import com.android.aftools.presentation.viewmodels.EnterPasswordVM
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for entering password
 */
@AndroidEntryPoint
class EnterPassFragment: Fragment() {
  private val viewModel: EnterPasswordVM by viewModels()
  private var _binding: EnterPassFragmentBinding? = null
  private val controller by lazy { findNavController() }
  private val binding
    get() = _binding ?: throw RuntimeException("MainFragmentBinding == null")

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
    if (activity is ActivityStateHolder) {
      activity.setActivityState(ActivityState.NoActionBarNoDrawerActivityState)
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
    binding.password.setOnEditorActionListener {
        textView, actionId, _ ->
        var handled = false
        if (actionId == IME_ACTION_SEND) {
          viewModel.passwordEntered(textView.text.toList().toCharArray())
          handled = true
        }
        return@setOnEditorActionListener handled
    }
  }

  private fun observePasswordState() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.passwordStatus.collect {
        when(it) {
          is EnterPasswordState.CheckEnterPasswordResults -> handlePasswordEntered(it.rightPassword)
          is EnterPasswordState.EnteringText -> setupError(null)
          is EnterPasswordState.Initial -> setupError(null)
        }
        if (it is EnterPasswordState.CheckEnterPasswordResults && it.rightPassword) {
          moveToNextScreen()
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
      isErrorEnabled = errorText == null
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
    controller.navigate(R.id.action_passFragmentNav_to_settingsFragment)
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
