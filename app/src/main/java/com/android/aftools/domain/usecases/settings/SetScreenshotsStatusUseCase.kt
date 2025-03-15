package com.android.aftools.domain.usecases.settings

import com.android.aftools.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetScreenshotsStatusUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        settingsRepository.setScreenshotsStatus(status)
    }
}