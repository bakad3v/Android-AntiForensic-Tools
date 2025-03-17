package com.sonozaki.profiles.domain.usecases

import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import javax.inject.Inject

class SetProfileDeletionStatusUseCase @Inject constructor(private val profilesScreenRepository: ProfilesScreenRepository) {
    suspend operator fun invoke(id: Int, status: Boolean) {
        return profilesScreenRepository.setProfileDeletionStatus(id, status)
    }
}