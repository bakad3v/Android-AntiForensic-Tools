package net.typeblog.shelter.domain.usecases.settings

import net.typeblog.shelter.domain.repositories.SettingsRepository
import javax.inject.Inject

class GetSafeBootRestrictionUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(): Boolean {
        return settingsRepository.getSafeBootStatus()
    }
}