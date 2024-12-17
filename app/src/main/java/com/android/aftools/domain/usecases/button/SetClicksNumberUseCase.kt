package com.android.aftools.domain.usecases.button

import com.android.aftools.domain.entities.ButtonSettings
import com.android.aftools.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetClicksNumberUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(allowedClicks: Int) {
        repository.updateAllowedClicks(allowedClicks)
    }
}