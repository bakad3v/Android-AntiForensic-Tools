package com.sonozaki.profiles.domain.usecases

import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeleteProfilesUseCase @Inject constructor(private val repository: ProfilesScreenRepository) {
  operator fun invoke(): Flow<Boolean> {
    return repository.deleteProfiles
  }
}
