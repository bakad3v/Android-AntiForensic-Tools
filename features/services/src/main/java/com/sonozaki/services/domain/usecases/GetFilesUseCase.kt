package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.entities.FileDomain
import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(): List<FileDomain> {
        return repository.getFiles()
    }
}