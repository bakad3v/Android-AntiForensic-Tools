package com.oasisfeng.island.domain.usecases.button

import com.oasisfeng.island.domain.entities.ButtonSettings
import com.oasisfeng.island.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetButtonSettingsUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    operator fun invoke(): Flow<ButtonSettings> = repository.buttonSettings
}