package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetDeleteAppsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(new: Boolean) {
        repository.setDeleteApps(new)
    }
}