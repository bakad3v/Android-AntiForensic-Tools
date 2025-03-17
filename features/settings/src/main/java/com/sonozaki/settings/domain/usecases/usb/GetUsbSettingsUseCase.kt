package com.sonozaki.settings.domain.usecases.usb

import com.sonozaki.entities.UsbSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsbSettingsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
  operator fun invoke(): Flow<UsbSettings> {
    return repository.usbSettings
  }
}
