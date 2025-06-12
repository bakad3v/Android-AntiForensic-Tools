package com.sonozaki.settings.domain.usecases.button

import com.sonozaki.entities.VolumeButtonTriggerOptions
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetTriggerOnVolumeButtonUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    suspend operator fun invoke(status: VolumeButtonTriggerOptions) {
        repository.setTriggerOnVolumeButtonStatus(status)
    }
}