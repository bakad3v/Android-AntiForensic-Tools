package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetSafeBootRestrictionUseCase @Inject constructor(private val settingsScreenRepository: SettingsScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        settingsScreenRepository.setSafeBootStatus(status)
    }
}