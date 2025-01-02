package net.typeblog.shelter.domain.usecases.button

import net.typeblog.shelter.domain.entities.ButtonSettings
import net.typeblog.shelter.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetTriggerOnButtonUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setTriggerOnButtonStatus(status)
    }
}