package com.bakasoft.setupwizard.presentation.states

import com.bakasoft.setupwizard.domain.entities.AvailableTriggers

sealed class TriggersConfirmationState() {
    data object Loading: TriggersConfirmationState()
    data class Data(val availableTriggers: AvailableTriggers): TriggersConfirmationState()
}