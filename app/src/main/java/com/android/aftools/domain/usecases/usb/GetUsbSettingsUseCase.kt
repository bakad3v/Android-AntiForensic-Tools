package com.android.aftools.domain.usecases.usb

import com.android.aftools.domain.entities.UsbSettings
import com.android.aftools.domain.repositories.UsbSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsbSettingsUseCase @Inject constructor(private val repository: UsbSettingsRepository) {
    operator fun invoke(): Flow<UsbSettings> {
        return repository.usbSettings
    }
}