package com.oasisfeng.island.domain.usecases.usb

import com.oasisfeng.island.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbStatusUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setUsbConnectionStatus(status)
    }
}