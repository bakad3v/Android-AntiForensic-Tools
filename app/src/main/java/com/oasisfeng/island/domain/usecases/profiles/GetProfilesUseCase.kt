package com.oasisfeng.island.domain.usecases.profiles

import com.oasisfeng.island.domain.entities.ProfileDomain
import com.oasisfeng.island.domain.repositories.ProfilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    operator fun invoke(): Flow<List<ProfileDomain>?> {
        return profilesRepository.getProfiles()
    }
}