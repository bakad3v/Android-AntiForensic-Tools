package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.ButtonSettings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetButtonSettingsUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(): ButtonSettings {
        return repository.getButtonSettings()
    }
}