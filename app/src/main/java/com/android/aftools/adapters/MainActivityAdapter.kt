package com.android.aftools.adapters

import com.android.aftools.domain.repository.MainActivityRepository
import com.sonozaki.entities.UISettings
import com.sonozaki.settings.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainActivityAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository
): MainActivityRepository {
    override val uiSettings: Flow<UISettings>
        get() = settingsRepository.settings.map { it.uiSettings }
}