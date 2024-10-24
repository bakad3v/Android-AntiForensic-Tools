package com.android.aftools.domain.usecases.profiles

import com.android.aftools.domain.repositories.ProfilesRepository
import javax.inject.Inject

class RefreshProfilesUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    suspend operator fun invoke() {
        return profilesRepository.refreshDeviceProfiles()
    }
}