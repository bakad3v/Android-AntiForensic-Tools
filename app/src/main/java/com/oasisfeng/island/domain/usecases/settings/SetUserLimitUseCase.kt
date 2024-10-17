package com.oasisfeng.island.domain.usecases.settings

import com.oasisfeng.island.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetUserLimitUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(limit: Int) {
        repository.setUserLimit(limit)
    }
}