package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetLogdOnStartUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
    suspend operator fun invoke(new: Boolean) {
        repository.setLogdOnStart(new)
    }
}