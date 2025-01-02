package com.oasisfeng.island.domain.usecases.settings

import com.oasisfeng.island.domain.entities.Theme
import com.oasisfeng.island.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(private val repository: SettingsRepository) {
  suspend operator fun invoke(theme: Theme) {
    repository.setTheme(theme)
  }
}
