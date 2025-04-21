package com.sonozaki.triggerreceivers.services.domain.usecases

import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.triggerreceivers.services.domain.repository.ReceiversRepository
import javax.inject.Inject

class GetDeviceProtectionSettings @Inject constructor(private val receiversRepository: ReceiversRepository) {
    suspend operator fun invoke(): DeviceProtectionSettings {
        return receiversRepository.getDeviceProtectionSettings()
    }
}