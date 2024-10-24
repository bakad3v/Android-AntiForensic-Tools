package com.android.aftools.domain.usecases.settings

import com.android.aftools.domain.entities.Theme
import com.android.aftools.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(private val repository: SettingsRepository) {
  suspend operator fun invoke(theme: Theme) {
    repository.setTheme(theme)
  }
}
