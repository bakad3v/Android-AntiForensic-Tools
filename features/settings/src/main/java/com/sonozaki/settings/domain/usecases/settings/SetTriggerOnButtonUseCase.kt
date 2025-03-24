package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetTriggerOnButtonUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setTriggerOnButtonStatus(status)
    }
}