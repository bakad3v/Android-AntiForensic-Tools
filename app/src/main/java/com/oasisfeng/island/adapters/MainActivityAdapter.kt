package com.oasisfeng.island.adapters

import com.oasisfeng.island.domain.repository.MainActivityRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainActivityAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository
): MainActivityRepository {
    override val uiSettings: Flow<UISettings>
        get() = settingsRepository.settings.map { it.uiSettings }
}