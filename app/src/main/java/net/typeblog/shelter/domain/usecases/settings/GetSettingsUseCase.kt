package net.typeblog.shelter.domain.usecases.settings

import net.typeblog.shelter.domain.entities.Settings
import net.typeblog.shelter.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
  operator fun invoke(): Flow<Settings> {
    return repository.settings
  }
}
