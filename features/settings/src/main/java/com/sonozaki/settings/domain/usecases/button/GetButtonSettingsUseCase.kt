package com.sonozaki.settings.domain.usecases.button

import com.sonozaki.entities.ButtonSettings
import com.sonozaki.settings.domain.repository.SettingsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetButtonSettingsUseCase @Inject constructor(val repository: SettingsScreenRepository) {
    operator fun invoke(): Flow<ButtonSettings> = repository.buttonSettings
}