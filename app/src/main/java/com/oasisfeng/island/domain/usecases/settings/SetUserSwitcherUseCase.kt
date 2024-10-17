package com.oasisfeng.island.domain.usecases.settings

import com.oasisfeng.island.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetUserSwitcherUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        settingsRepository.setUserSwitcherStatus(status)
    }
}