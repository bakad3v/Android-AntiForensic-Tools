package com.oasisfeng.island.domain.usecases.settings

import com.oasisfeng.island.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetRunOnBootUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRunOnBoot(status)
    }
}
