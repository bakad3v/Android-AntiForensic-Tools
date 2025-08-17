package com.sonozaki.profiles.domain.usecases

import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import javax.inject.Inject

class OpenProfileUseCase @Inject constructor(private val profilesScreenRepository: ProfilesScreenRepository) {
    suspend operator fun invoke(id: Int) {
        return profilesScreenRepository.openProfile(id)
    }
}