package net.typeblog.shelter.domain.usecases.profiles

import net.typeblog.shelter.domain.repositories.ProfilesRepository
import javax.inject.Inject

class RefreshProfilesUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    suspend operator fun invoke() {
        return profilesRepository.refreshDeviceProfiles()
    }
}