package net.typeblog.shelter.domain.usecases.settings

import net.typeblog.shelter.domain.entities.Theme
import net.typeblog.shelter.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(private val repository: SettingsRepository) {
  suspend operator fun invoke(theme: Theme) {
    repository.setTheme(theme)
  }
}
