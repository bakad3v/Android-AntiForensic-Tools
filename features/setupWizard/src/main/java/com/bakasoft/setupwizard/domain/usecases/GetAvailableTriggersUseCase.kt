package com.bakasoft.setupwizard.domain.usecases

import com.bakasoft.setupwizard.domain.entities.AvailableTriggers
import com.bakasoft.setupwizard.domain.repository.SetupWizardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAvailableTriggersUseCase @Inject constructor(
    private val repository: SetupWizardRepository
) {
    operator fun invoke(): Flow<AvailableTriggers> {
        return combine(repository.permissions, repository.settings,
        repository.buttonSettings, repository.bruteforceSettings) {
            permissions, settings, buttonSettings, bruteforceSettings ->
            if (!settings.serviceWorking) {
                AvailableTriggers.NoTriggers
            } else if (!permissions.isRoot) {
                val clicks = buttonSettings.volumeButtonAllowedClicks
                val allowedAttempts = bruteforceSettings.allowedAttempts
                AvailableTriggers.VolumeButton(clicks, allowedAttempts)
            } else {
                val clicks = buttonSettings.allowedClicks
                val allowedAttempts = bruteforceSettings.allowedAttempts
                AvailableTriggers.PowerButton(clicks, allowedAttempts)
            }
        }
    }
}