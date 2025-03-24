package com.oasisfeng.island.domain.usecases

import com.oasisfeng.island.domain.repository.MainActivityRepository
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUISettingsUseCase @Inject constructor(private val repository: MainActivityRepository) {
    operator fun invoke(): Flow<UISettings> {
        return repository.uiSettings
    }
}