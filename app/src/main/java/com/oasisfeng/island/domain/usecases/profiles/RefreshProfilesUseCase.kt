package com.oasisfeng.island.domain.usecases.profiles

import com.oasisfeng.island.domain.repositories.ProfilesRepository
import javax.inject.Inject

class RefreshProfilesUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    suspend operator fun invoke() {
        return profilesRepository.refreshDeviceProfiles()
    }
}