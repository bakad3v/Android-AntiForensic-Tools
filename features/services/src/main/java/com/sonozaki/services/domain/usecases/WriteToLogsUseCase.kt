package com.sonozaki.services.domain.usecases

import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class WriteToLogsUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(text: String) {
        repository.writeToLogs(text)
    }
}