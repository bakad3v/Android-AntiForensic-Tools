package com.android.aftools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.aftools.domain.usecases.logs.GetLogsDataUseCase
import com.android.aftools.domain.usecases.logs.WriteToLogsUseCase
import com.android.aftools.domain.usecases.passwordManager.CheckPasswordUseCase
import com.android.aftools.presentation.states.EnterPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
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
        SharingStarted.WhileSubscribed(5000),
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
        if (getLogsDataUseCase().first().logsEnabled)
            writeToLogsUseCase(string)
    }
}
