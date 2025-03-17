package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class GetUserSwitcherStatusUseCase @Inject constructor(private val settingsScreenRepository: SettingsScreenRepository) {
    suspend operator fun invoke(): Boolean {
        return settingsScreenRepository.getUserSwitcherStatus()
    }
}