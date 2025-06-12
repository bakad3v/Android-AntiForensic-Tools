package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetButtonsRootDataUseCase @Inject constructor(private val repository: ReceiversRepository) {
    operator fun invoke(): Flow<Boolean> {
        return combine(repository.getButtonSettingsFlow(), repository.getPermissionsFlow()) { buttonSettings: ButtonSettings, permissions: Permissions ->
            permissions.isRoot && buttonSettings.triggerOnButton == PowerButtonTriggerOptions.SUPERUSER_WAY
        }
    }
}