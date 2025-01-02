package net.typeblog.shelter.domain.usecases.settings

import net.typeblog.shelter.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetServiceStatusUseCase @Inject constructor(private val repository: SettingsRepository) {
  suspend operator fun invoke(working: Boolean) {
    repository.setServiceStatus(working)
  }
}
