package com.sonozaki.lockscreen.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.lockscreen.domain.usecases.CheckPasswordUseCase
import com.sonozaki.lockscreen.domain.usecases.GetLogsDataUseCase
import com.sonozaki.lockscreen.domain.usecases.WriteToLogsUseCase
import com.sonozaki.lockscreen.presentation.state.EnterPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterPasswordVM @Inject constructor(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val enterPasswordState: MutableSharedFlow<EnterPasswordState>,
    private val writeToLogsUseCase: WriteToLogsUseCase,
    private val getLogsDataUseCase: GetLogsDataUseCase
) : ViewModel() {
    val passwordStatus = enterPasswordState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        EnterPasswordState.Initial
    )

    fun passwordEntered(password: CharArray) {
        viewModelScope.launch {
            checkPassword(password)
        }
    }

    fun setTextChangedState() {
        viewModelScope.launch {
            enterPasswordState.emit(EnterPasswordState.EnteringText)
        }
    }

    private suspend fun checkPassword(password: CharArray) {
        val results = checkPasswordUseCase(password)
        enterPasswordState.emit(EnterPasswordState.CheckEnterPasswordResults(results))
    }

    suspend fun writeToLogs(string: String) {
        if (getLogsDataUseCase().logsEnabled)
            writeToLogsUseCase(string)
    }
}
