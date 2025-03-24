package com.sonozaki.settings.domain.usecases.permissions

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetOwnerActiveUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(active: Boolean) {
        repository.setOwnerStatus(active)
    }
}