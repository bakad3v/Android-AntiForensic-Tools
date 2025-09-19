package com.sonozaki.profiles.domain.usecases

import com.sonozaki.entities.Permissions
import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsFlowUseCase @Inject constructor(
    private val profilesScreenRepository: ProfilesScreenRepository
) {
    operator fun invoke(): Flow<Permissions> = profilesScreenRepository.permissions
}