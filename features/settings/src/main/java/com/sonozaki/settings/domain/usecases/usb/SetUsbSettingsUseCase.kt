package com.sonozaki.settings.domain.usecases.usb

import com.sonozaki.entities.UsbSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetUsbSettingsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(settings: UsbSettings) {
        repository.setUsbSettings(settings)
    }
}