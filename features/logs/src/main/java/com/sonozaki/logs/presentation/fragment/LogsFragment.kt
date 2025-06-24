package com.sonozaki.logs.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.dialogs.DialogLauncher
import com.sonozaki.dialogs.QuestionDialog
import com.sonozaki.logs.R
import com.sonozaki.logs.databinding.LogsFragmentBinding
import com.sonozaki.logs.presentation.actions.LogsActions
import com.sonozaki.logs.presentation.state.LogsDataState
import com.sonozaki.logs.presentation.viewmodel.LogsVM
import com.sonozaki.logs.presentation.viewmodel.LogsVM.Companion.CHANGE_LOGS_ENABLED_REQUEST
import com.sonozaki.logs.presentation.viewmodel.LogsVM.Companion.CHANGE_TIMEOUT
import com.sonozaki.logs.presentation.viewmodel.LogsVM.Companion.CLEAR_LOGS_REQUEST
import com.sonozaki.utils.DateValidatorAllowed
import com.sonozaki.utils.TopLevelFunctions.formatDate
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying logs
 */
@AndroidEntryPoint
class LogsFragment : Fragment() {
  private val viewModel: LogsVM by viewModels()
  private var _logBinding: LogsFragmentBinding? = null
  private val logBinding
    get() = _logBinding ?: throw RuntimeException("LogsFragmentBinding == null")
  private val dialogLauncher by lazy {
      DialogLauncher(
          parentFragmentManager,
          context
      )
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _logBinding =
      LogsFragmentBinding.inflate(inflater, container, false)
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
      CLEAR_LOGS_REQUEST,
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
    com.sonozaki.dialogs.InputDigitDialog.setupListener(parentFragmentManager, viewLifecycleOwner,CHANGE_TIMEOUT) {
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
        icon = com.sonozaki.resources.R.drawable.ic_baseline_pause_24
        text = R.string.disable_logs
      } else {
        icon = com.sonozaki.resources.R.drawable.ic_baseline_play_arrow_24
        text = R.string.enable_logs
      }
      val startIcon = findItem(R.id.logs_enabled)
      startIcon?.setIcon(icon)?.setTitle(text)
    }
  }

  /**
   * Setting up date in action bar
   */
  private fun setupActionBar() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.logsState.collect {
        setupActionBar(it.date.formatDate())
        when (it) {
          is LogsDataState.ViewLogs -> {
            setupDataVisibility(true)
            logBinding.data.text = HtmlCompat.fromHtml(it.logs.asString(context), FROM_HTML_MODE_LEGACY)
            scrollToBottom()
          }
          is LogsDataState.Loading -> {
            setupDataVisibility(false)
          }
        }
      }
    }
  }

  private fun scrollToBottom() {
    with(logBinding.scrollView3) {
      post {
        run {
          fullScroll(View.FOCUS_DOWN)
        }
      }
    }
  }

  private fun setupDataVisibility(visible: Boolean) {
    with(logBinding) {
      progressBar3.visibility = com.sonozaki.utils.booleanToVisibility(!visible)
      scrollView3.visibility = com.sonozaki.utils.booleanToVisibility(visible)
    }
  }

  private fun setupActionBar(date: String) {
    val activity = requireActivity()
    if (activity is ActivityStateHolder) {
      activity.setActivityState(
        ActivityState.NormalActivityState(date)
      )
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
