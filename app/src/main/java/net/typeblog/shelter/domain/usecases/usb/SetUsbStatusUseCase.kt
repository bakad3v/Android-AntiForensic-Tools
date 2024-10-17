package net.typeblog.shelter.domain.usecases.usb

import net.typeblog.shelter.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbStatusUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setUsbConnectionStatus(status)
    }
}