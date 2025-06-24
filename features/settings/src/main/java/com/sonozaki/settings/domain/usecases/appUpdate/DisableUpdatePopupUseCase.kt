package com.sonozaki.settings.domain.usecases.appUpdate

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class DisableUpdatePopupUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke() {
        repository.disableUpdatePopup()
    }
}