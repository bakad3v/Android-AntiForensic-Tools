package com.sonozaki.settings.domain.usecases.button

import com.sonozaki.entities.PowerButtonTriggerOptions
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetTriggerOnPowerButtonUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: PowerButtonTriggerOptions) {
        repository.setTriggerOnButtonStatus(status)
    }
}