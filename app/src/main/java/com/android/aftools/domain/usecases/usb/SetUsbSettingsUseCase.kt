package com.android.aftools.domain.usecases.usb

import com.android.aftools.domain.entities.UsbSettings
import com.android.aftools.domain.repositories.UsbSettingsRepository
import javax.inject.Inject

class SetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    suspend operator fun invoke(settings: UsbSettings) {
        repository.setUsbSettings(settings)
    }
}