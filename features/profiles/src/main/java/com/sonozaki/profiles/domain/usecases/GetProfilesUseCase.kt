package com.sonozaki.profiles.domain.usecases

import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(private val profilesScreenRepository: ProfilesScreenRepository) {
    operator fun invoke(): Flow<List<ProfileDomain>?> {
        return profilesScreenRepository.getProfiles()
    }
}