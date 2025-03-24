package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetRunOnDuressUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRunOnDuressPassword(status)
    }
}
