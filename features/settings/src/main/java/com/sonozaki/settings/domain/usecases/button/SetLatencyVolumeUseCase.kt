package com.sonozaki.settings.domain.usecases.button

import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetLatencyVolumeUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(latency: Int) {
        repository.setTriggerOnVolumeButtonLatency(latency)
    }
}