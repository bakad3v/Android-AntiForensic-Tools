package com.bakasoft.setupwizard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakasoft.setupwizard.domain.usecases.GetAvailableProtectionUseCase
import com.bakasoft.setupwizard.domain.usecases.SetupProtectionAutomaticallyUseCase
import com.bakasoft.setupwizard.presentation.actions.ConfirmationFragmentsActions
import com.bakasoft.setupwizard.presentation.states.ProtectionConfirmationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupProtectionConfirmationVM @Inject constructor(
    getAvailableProtectionUseCase: GetAvailableProtectionUseCase,
    private val _actionsChannel: Channel<ConfirmationFragmentsActions>,
    private val setupProtectionAutomaticallyUseCase: SetupProtectionAutomaticallyUseCase
): ViewModel() {

    val actionsFlow = _actionsChannel.receiveAsFlow()

    val availableProtectionState = getAvailableProtectionUseCase().map {
        ProtectionConfirmationState.Data(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ProtectionConfirmationState.Loading
    )

    fun navigateBack() {
        viewModelScope.launch {
            _actionsChannel.send(ConfirmationFragmentsActions.NAVIGATE_BACK)
        }
    }

    fun setupProtection() {
        viewModelScope.launch {
            val state = availableProtectionState.value
            if (state is ProtectionConfirmationState.Data) {
                setupProtectionAutomaticallyUseCase(state.availableProtection)
            }
            _actionsChannel.send(ConfirmationFragmentsActions.NAVIGATE_BACK)
        }
    }
}