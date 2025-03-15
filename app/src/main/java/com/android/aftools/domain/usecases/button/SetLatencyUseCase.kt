package com.android.aftools.domain.usecases.button

import com.android.aftools.domain.repositories.ButtonSettingsRepository
import javax.inject.Inject

class SetLatencyUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(latency: Int) {
        repository.updateLatency(latency)
    }
}