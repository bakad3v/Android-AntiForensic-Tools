package com.sonozaki.settings.domain.usecases.permissions

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetRootActiveUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRootStatus(status)
    }
}
