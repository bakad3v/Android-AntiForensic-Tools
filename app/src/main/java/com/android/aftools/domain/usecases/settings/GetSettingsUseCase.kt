package com.android.aftools.domain.usecases.settings

import com.android.aftools.domain.entities.Settings
import com.android.aftools.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
  operator fun invoke(): Flow<Settings> {
    return repository.settings
  }
}
