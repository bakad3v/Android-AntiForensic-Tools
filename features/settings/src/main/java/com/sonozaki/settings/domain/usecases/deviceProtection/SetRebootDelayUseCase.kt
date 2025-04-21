package com.sonozaki.settings.domain.usecases.deviceProtection

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetRebootDelayUseCase @Inject constructor(
    private val settingsScreenRepository: SettingsScreenRepository
) {
    suspend operator fun invoke(delay: Int) {
        settingsScreenRepository.changeRebootDelay(delay)
    }
}