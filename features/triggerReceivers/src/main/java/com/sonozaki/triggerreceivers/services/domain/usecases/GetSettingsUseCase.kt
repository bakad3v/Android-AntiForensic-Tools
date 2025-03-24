package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.Settings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(): Settings {
        return repository.getSettings()
    }
}