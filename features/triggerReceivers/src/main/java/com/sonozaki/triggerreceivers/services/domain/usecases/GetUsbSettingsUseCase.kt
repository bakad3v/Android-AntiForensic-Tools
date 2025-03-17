package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.UsbSettings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetUsbSettingsUseCase @Inject constructor(private val repository: ReceiversRepository) {
    suspend operator fun invoke(): UsbSettings {
        return repository.getUsbSettings()
    }
}