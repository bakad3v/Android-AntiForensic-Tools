package com.sonozaki.profiles.domain.usecases

import com.sonozaki.profiles.domain.repository.ProfilesScreenRepository
import javax.inject.Inject

class SetDeleteProfilesUseCase @Inject constructor(private val repository: ProfilesScreenRepository) {
    suspend operator fun invoke(new: Boolean) {
        repository.setDeleteProfiles(new)
    }
}