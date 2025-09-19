package com.bakasoft.setupwizard.presentation.states

import com.bakasoft.setupwizard.domain.entities.AvailableProtection

sealed class ProtectionConfirmationState {
    data object Loading: ProtectionConfirmationState()
    data class Data(val availableProtection: AvailableProtection): ProtectionConfirmationState()
}