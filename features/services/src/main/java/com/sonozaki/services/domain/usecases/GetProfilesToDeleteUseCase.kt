package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetProfilesToDeleteUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(): List<Int> {
        return repository.getProfilesToDelete()
    }
}