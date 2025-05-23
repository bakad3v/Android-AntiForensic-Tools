package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class RemoveProfileFromDeletionUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(profile: Int) {
        repository.removeProfileFromDeletion(profile)
    }
}