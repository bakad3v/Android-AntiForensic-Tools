package net.typeblog.shelter.domain.usecases.profiles

import net.typeblog.shelter.domain.entities.ProfileDomain
import net.typeblog.shelter.domain.repositories.ProfilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    operator fun invoke(): Flow<List<ProfileDomain>?> {
        return profilesRepository.getProfiles()
    }
}