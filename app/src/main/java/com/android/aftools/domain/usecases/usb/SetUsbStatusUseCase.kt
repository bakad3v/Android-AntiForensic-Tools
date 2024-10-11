package com.android.aftools.domain.usecases.usb

import com.android.aftools.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbStatusUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setUsbConnectionStatus(status)
    }
}