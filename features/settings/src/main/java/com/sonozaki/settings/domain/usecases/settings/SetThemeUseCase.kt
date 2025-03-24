package com.sonozaki.settings.domain.usecases.settings

import com.sonozaki.entities.Theme
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(private val repository: SettingsScreenRepository) {
  suspend operator fun invoke(theme: Theme) {
    repository.setTheme(theme)
  }
}
