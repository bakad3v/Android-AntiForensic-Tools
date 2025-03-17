package com.sonozaki.settings.domain.usecases.button

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetClicksNumberUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(allowedClicks: Int) {
        repository.updateAllowedClicks(allowedClicks)
    }
}