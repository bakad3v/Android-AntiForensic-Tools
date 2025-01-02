package net.typeblog.shelter.domain.usecases.button

import net.typeblog.shelter.domain.entities.ButtonSettings
import net.typeblog.shelter.domain.repositories.ButtonSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetClicksNumberUseCase @Inject constructor(val repository: ButtonSettingsRepository) {
    suspend operator fun invoke(allowedClicks: Int) {
        repository.updateAllowedClicks(allowedClicks)
    }
}