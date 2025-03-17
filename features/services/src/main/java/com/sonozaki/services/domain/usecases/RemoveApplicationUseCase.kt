package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class RemoveApplicationUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(packageName: String) {
        repository.removeApplication(packageName)
    }
}