package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetBruteforceSettingsUseCase @Inject constructor(val repository: ReceiversRepository) {
    suspend operator fun invoke(): BruteforceSettings {
        return repository.getBruteforceSettings()
    }
}