package com.oasisfeng.island.domain.usecases.settings

import com.oasisfeng.island.domain.entities.Settings
import com.oasisfeng.island.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
  operator fun invoke(): Flow<Settings> {
    return repository.settings
  }
}
