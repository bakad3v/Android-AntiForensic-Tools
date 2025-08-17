package com.bakasoft.setupwizard.domain.entities

sealed class AvailableTriggers {
    data object NoTriggers: AvailableTriggers()
    data class VolumeButton(val clicks: Int, val allowedAttempts: Int): AvailableTriggers()
    data class PowerButton(val clicks: Int, val allowedAttempts: Int): AvailableTriggers()
}