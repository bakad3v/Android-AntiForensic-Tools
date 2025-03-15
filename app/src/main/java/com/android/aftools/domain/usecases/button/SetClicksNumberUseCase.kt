package com.android.aftools.domain.usecases.button

import com.android.aftools.domain.repositories.ButtonSettingsRepository
import javax.inject.Inject

class SetClicksNumberUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(allowedClicks: Int) {
        repository.updateAllowedClicks(allowedClicks)
    }
}