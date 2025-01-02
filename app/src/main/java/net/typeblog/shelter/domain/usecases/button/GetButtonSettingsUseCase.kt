package net.typeblog.shelter.domain.usecases.button

import net.typeblog.shelter.domain.entities.ButtonSettings
import net.typeblog.shelter.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetButtonSettingsUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    operator fun invoke(): Flow<ButtonSettings> = repository.buttonSettings
}