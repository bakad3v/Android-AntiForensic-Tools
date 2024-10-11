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
import com.android.aftools.TopLevelFunctions.formatDate
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.LogsFragmentBinding
import com.android.aftools.presentation.actions.LogsActions
import com.android.aftools.presentation.activities.MainActivity
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.InputDigitDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.utils.DateValidatorAllowed
import com.android.aftools.presentation.viewmodels.LogsVM
import com.android.aftools.presentation.viewmodels.LogsVM.Companion.CHANGE_LOGS_ENABLED_REQUEST
import com.android.aftools.presentation.viewmodels.LogsVM.Companion.CHANGE_TIMEOUT_REQUEST
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.viewmodels.LogsVM.Companion.CHANGE_TIMEOUT
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fragment for displaying logs
 */
@AndroidEntryPoint
class LogsFragment : Fragment() {
  private val viewModel: LogsVM by viewModels()
  private var _logBinding: LogsFragmentBinding? = null
  private val logBinding
    get() = _logBinding ?: throw RuntimeException("LogsFragmentBinding == null")

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _logBinding =
      LogsFragmentBinding.inflate(inflater, container, false)
    logBinding.viewmodel = viewModel
    logBinding.lifecycleOwner = viewLifecycleOwner
    return logBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupActionBar()
    setDialogsListeners()
    setupActionsListener()
    setupMenu()
  }

  /**
   * Setting up menu
   */
  private fun setupMenu() {
    requireActivity().addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
          menuInflater.inflate(R.menu.logs_menu, menu)
          menu.drawSwitchLogsStatusButton()
        }
      }


      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
          R.id.logs_enabled -> {
            viewModel.changeLogsEnabledQuestion()
          }
          R.id.calendar -> {
            viewModel.buildCalendar() //обновляем логи в приложении
          }

          R.id.log_timeout -> viewModel.showChangeTimeoutDialog() //изменение тайм-аута очистки логов
          R.id.clear_logs -> viewModel.showClearLogsDialog() //очистка логов за день
          R.id.logs_help -> viewModel.showHelpDialog()

        }
        return true
      }
    },viewLifecycleOwner,Lifecycle.State.RESUMED)
  }

  /**
   * Launching dialogs or Datepicker
   */
  private fun setupActionsListener() {
    val dialogLauncher = DialogLauncher(parentFragmentManager, context)
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.logsActionFlow.collect {
        when (it) {
          is LogsActions.ShowUsualDialog -> dialogLauncher.launchDialogFromAction(it.value)
          is LogsActions.ShowDatePicker -> with(it) { buildCalendar(dateValidator, selection) }
        }
      }
    }
  }

  /**
   * Building calendar for Datepicker
   */
  private fun buildCalendar(dateValidatorAllowed: DateValidatorAllowed, selection: Long) {
    val constraintsBuilder =
      getConstraints(dateValidatorAllowed) //Настраиваем ограничения для date picker, разрешаем выбирать те дни, за которые доступны логи.
    val datePicker =
      makeDatePicker(constraintsBuilder, selection)
    datePicker.show(parentFragmentManager, MATERIAL_PICKER_TAG) //показываем календарь
    datePicker.addOnPositiveButtonClickListener {
      viewModel.openLogsForSelection(it)
    }
  }

  /**
   * Creating Datepicker
   */
  private fun makeDatePicker(constraintsBuilder: CalendarConstraints.Builder, selection: Long) =
    MaterialDatePicker.Builder.datePicker()
      .setTitleText(getString(R.string.select_date))
      .setSelection(
        selection
      )
      .setCalendarConstraints(constraintsBuilder.build())
      .build()

  private fun getConstraints(dateValidatorAllowed: DateValidatorAllowed) =
    CalendarConstraints.Builder()
      .setValidator(
        dateValidatorAllowed
      )

  /**
   * Listening for dialogs results
   */
  private fun setDialogsListeners() {
    QuestionDialog.setupListener(
      parentFragmentManager,
      CHANGE_TIMEOUT_REQUEST,
      viewLifecycleOwner
    ) {
      viewModel.clearLogsForDay()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      CHANGE_LOGS_ENABLED_REQUEST,
      viewLifecycleOwner
    ) {
      viewModel.changeLogsEnabled()
    }
    InputDigitDialog.setupListener(parentFragmentManager, viewLifecycleOwner,CHANGE_TIMEOUT) {
      viewModel.changeAutoDeletionTimeout(it)
    }
  }

  /**
   * Rendering button for starting or stopping logging
   */
  private suspend fun Menu.drawSwitchLogsStatusButton() {
    viewModel.logsEnabled.collect {
      val icon: Int
      val text: Int
      if (it) {
        icon = R.drawable.ic_baseline_pause_24
        text = R.string.disable_logs
      } else {
        icon = R.drawable.ic_baseline_play_arrow_24
        text = R.string.enable_logs
      }
      withContext(Dispatchers.Main) {
        val startIcon = findItem(R.id.logs_enabled)
          ?: throw RuntimeException("Enable logs button not found")
        startIcon.setIcon(icon).setTitle(text)
      }
    }
  }

  /**
   * Setting up date in action bar
   */
  private fun setupActionBar() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.logsState.collect {
        (activity as MainActivity).setActivityState(
          ActivityState.NormalActivityState(
            it.date.formatDate()
          )
        )
      }
    }
  }

  override fun onDestroyView() {
    _logBinding = null
    super.onDestroyView()
  }

  companion object {
    private const val MATERIAL_PICKER_TAG = "material_picker_tag"
  }
}
