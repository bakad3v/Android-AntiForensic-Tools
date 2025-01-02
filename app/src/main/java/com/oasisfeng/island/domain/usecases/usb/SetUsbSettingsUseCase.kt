package com.oasisfeng.island.domain.usecases.usb

import com.oasisfeng.island.domain.entities.UsbSettings
import com.oasisfeng.island.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(settings: UsbSettings) {
        repository.setUsbSettings(settings)
    }
}