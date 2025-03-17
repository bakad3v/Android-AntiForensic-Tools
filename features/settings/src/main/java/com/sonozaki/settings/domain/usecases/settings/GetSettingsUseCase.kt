package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.entities.Settings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
  operator fun invoke(): Flow<Settings> {
    return repository.settings
  }
}
