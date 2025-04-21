package com.sonozaki.settings.domain.usecases.deviceProtection

import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceProtectionSettingsUseCase @Inject constructor(
    private val settingsScreenRepository: SettingsScreenRepository
) {
    operator fun invoke(): Flow<DeviceProtectionSettings> = settingsScreenRepository.deviceProtectionSettings
}