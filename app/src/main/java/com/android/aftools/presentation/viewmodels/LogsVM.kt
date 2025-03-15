package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.formatDate
import com.android.aftools.TopLevelFunctions.getMillis
import com.android.aftools.domain.usecases.logs.ChangeAutoDeletionTimeOutUseCase
import com.android.aftools.domain.usecases.logs.ChangeLogsEnabledUseCase
import com.android.aftools.domain.usecases.logs.ClearLogsForDayUseCase
import com.android.aftools.domain.usecases.logs.GetAvailableDaysUseCase
import com.android.aftools.domain.usecases.logs.GetLogsDataUseCase
import com.android.aftools.domain.usecases.logs.GetLogsUseCase
import com.android.aftools.domain.usecases.logs.LookLogsForDayUseCase
import com.android.aftools.presentation.dialogs.DialogActions
import com.android.aftools.presentation.actions.LogsActions
import com.android.aftools.presentation.states.LogsDataState
import com.android.aftools.presentation.utils.DateValidatorAllowed
import com.android.aftools.presentation.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class LogsVM @Inject constructor(
  getLogsUseCase: GetLogsUseCase,
  private val getLogsDataUseCase: GetLogsDataUseCase,
  private val changeAutoDeletionTimeOutUseCase: ChangeAutoDeletionTimeOutUseCase,
  private val clearLogsForDayUseCase: ClearLogsForDayUseCase,
  private val lookLogsForDayUseCase: LookLogsForDayUseCase,
  private val updateStatesFlow: MutableSharedFlow<LogsDataState>,
  private val logsActionsChannel: Channel<LogsActions>,
  private val changeLogsEnabledUseCase: ChangeLogsEnabledUseCase,
  private val getAvailableDaysUseCase: GetAvailableDaysUseCase
) : ViewModel() {
  val logsActionFlow = logsActionsChannel.receiveAsFlow()


  val logsEnabled = getLogsDataUseCase().map { it.logsEnabled }.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000),false
  )

  val logsState: StateFlow<LogsDataState> =
    getLogsUseCase().map {
      LogsDataState.ViewLogs(
        it.today,
        UIText.ColoredHTMLText(it.logs, com.google.android.material.R.attr.colorPrimary, com.google.android.material.R.attr.colorOnBackground)
      )
    }.mergeWith(updateStatesFlow).stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = LogsDataState.Loading()
    )

  private fun <T> Flow<T>.mergeWith(flow: Flow<T>): Flow<T> {
    return merge(this, flow)
  }

  fun changeAutoDeletionTimeout(timeout: Int) {
    viewModelScope.launch {
      changeAutoDeletionTimeOutUseCase(timeout)
    }
  }

  fun changeLogsEnabledQuestion() {
    viewModelScope.launch {
      if (logsEnabled.value) {
        changeLogsEnabled()
        return@launch
      }
      val title = R.string.enable_logs
      val text = R.string.enable_logs_long
      logsActionsChannel.send(
        LogsActions.ShowUsualDialog(
          DialogActions.ShowQuestionDialog(
            UIText.StringResource(title),
            UIText.StringResource(text),
            CHANGE_LOGS_ENABLED_REQUEST
          )
        )
      )

    }
  }

  fun changeLogsEnabled() {
    viewModelScope.launch {
      changeLogsEnabledUseCase()
    }
  }


  fun clearLogsForDay() {
    viewModelScope.launch {
      clearLogsForDayUseCase(logsState.value.date.formatDate())
    }
  }

  private fun getDateTimeFromMillis(millis: Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
  }

  fun openLogsForSelection(selection: Long) {
    viewModelScope.launch {
      updateStatesFlow.emit(LogsDataState.Loading())
      lookLogsForDayUseCase(getDateTimeFromMillis(selection))
    }
  }

  fun buildCalendar() {
    viewModelScope.launch {
      logsActionsChannel.send(
        LogsActions.ShowDatePicker(
          DateValidatorAllowed(getAvailableDaysUseCase().first().toSet()),
          logsState.value.date.getMillis()
        )
      )
    }
  }

  fun showChangeTimeoutDialog() {
    viewModelScope.launch {
      val logsData = getLogsDataUseCase().first()
      logsActionsChannel.send(
        LogsActions.ShowUsualDialog(
          DialogActions.ShowInputDigitDialog(
            UIText.StringResource(R.string.enter_timeout_logs),
            logsData.logsAutoRemovePeriod.toString(),
            UIText.StringResource(R.string.timeout_logs_long),
            1..10000,
            CHANGE_TIMEOUT
          )
        )
      )
    }
  }

  fun showClearLogsDialog() {
    viewModelScope.launch {
      val state = logsState.first()
      logsActionsChannel.send(
        LogsActions.ShowUsualDialog(
          DialogActions.ShowQuestionDialog(
            UIText.StringResource(R.string.clear_logs_question),
            UIText.StringResource(R.string.logs_clear_warning, state.date.formatDate()),
            CLEAR_LOGS_REQUEST
          )
        )
      )
    }
  }

  fun showHelpDialog() {
    viewModelScope.launch {
      logsActionsChannel.send(
        LogsActions.ShowUsualDialog(
          DialogActions.ShowInfoDialog(
            UIText.StringResource(R.string.help),
            UIText.StringResource(R.string.logs_help)
          )
        )
      )
    }
  }

  companion object {
    const val CLEAR_LOGS_REQUEST = "clear_logs_request"
    const val CHANGE_LOGS_ENABLED_REQUEST = "change_logs_enabled_request"
    const val CHANGE_TIMEOUT = "change_timeout"
  }
}
