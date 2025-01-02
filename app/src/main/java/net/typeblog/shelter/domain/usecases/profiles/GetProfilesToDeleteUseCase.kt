package net.typeblog.shelter.domain.usecases.profiles

import net.typeblog.shelter.domain.repositories.ProfilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilesToDeleteUseCase @Inject constructor(private val profilesRepository: ProfilesRepository) {
    operator fun invoke(): Flow<List<Int>> {
        return profilesRepository.getProfilesToDelete()
    }
}