package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetRootCommandUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(): String {
        return repository.getRootCommand()
    }
}