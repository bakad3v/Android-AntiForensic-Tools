package net.typeblog.shelter.domain.usecases.usb

import net.typeblog.shelter.domain.entities.UsbSettings
import net.typeblog.shelter.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(settings: UsbSettings) {
        repository.setUsbSettings(settings)
    }
}