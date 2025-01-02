package com.oasisfeng.island.domain.usecases.button

import com.oasisfeng.island.domain.entities.ButtonSettings
import com.oasisfeng.island.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetLatencyUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(latency: Int) {
        repository.updateLatency(latency)
    }
}