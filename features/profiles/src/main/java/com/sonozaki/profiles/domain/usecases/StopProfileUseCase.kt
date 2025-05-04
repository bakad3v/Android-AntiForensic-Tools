package com.sonozaki.profiles.domain.usecases

import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import javax.inject.Inject

class StopProfileUseCase @Inject constructor(private val repository: ProfilesScreenRepository) {
    suspend operator fun invoke(id: Int, isCurrent: Boolean) {
        repository.stopProfile(id, isCurrent)
    }
}