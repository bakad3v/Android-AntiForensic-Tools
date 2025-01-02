package com.oasisfeng.island.domain.usecases.usb

import com.oasisfeng.island.domain.entities.UsbSettings
import com.oasisfeng.island.domain.repositories.UsbSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    operator fun invoke(): Flow<UsbSettings> {
        return repository.usbSettings
    }
}