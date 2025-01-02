package net.typeblog.shelter.domain.usecases.usb

import net.typeblog.shelter.domain.entities.UsbSettings
import net.typeblog.shelter.domain.repositories.UsbSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    operator fun invoke(): Flow<UsbSettings> {
        return repository.usbSettings
    }
}