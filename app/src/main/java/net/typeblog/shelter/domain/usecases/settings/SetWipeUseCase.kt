package net.typeblog.shelter.domain.usecases.settings

import net.typeblog.shelter.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetWipeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(new: Boolean) {
        repository.setWipe(new)
    }
}