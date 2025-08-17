package com.bakasoft.setupwizard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakasoft.setupwizard.domain.usecases.GetAvailableTriggersUseCase
import com.bakasoft.setupwizard.domain.usecases.SetupTriggersAutomaticallyUseCase
import com.bakasoft.setupwizard.presentation.actions.ConfirmationFragmentsActions
import com.bakasoft.setupwizard.presentation.states.TriggersConfirmationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupTriggersConfirmationVM @Inject constructor(
    getAvailableTriggersUseCase: GetAvailableTriggersUseCase,
    private val _actionsChannel: Channel<ConfirmationFragmentsActions>,
    private val setupTriggersAutomaticallyUseCase: SetupTriggersAutomaticallyUseCase
): ViewModel() {

    val actionsFlow = _actionsChannel.receiveAsFlow()

    val availableTriggersState = getAvailableTriggersUseCase().map {
        TriggersConfirmationState.Data(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        TriggersConfirmationState.Loading
    )

    fun navigateBack() {
        viewModelScope.launch {
            _actionsChannel.send(ConfirmationFragmentsActions.NAVIGATE_BACK)
        }
    }

    fun setupTriggers() {
        viewModelScope.launch {
            val state = availableTriggersState.value
            if (state is TriggersConfirmationState.Data) {
                setupTriggersAutomaticallyUseCase(state.availableTriggers)
            }
            _actionsChannel.send(ConfirmationFragmentsActions.NAVIGATE_BACK)
        }
    }
}