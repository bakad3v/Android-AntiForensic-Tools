package com.sonozaki.services.domain.usecases

import com.sonozaki.entities.Settings
import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(): Settings {
        return repository.getSettings()
    }
}