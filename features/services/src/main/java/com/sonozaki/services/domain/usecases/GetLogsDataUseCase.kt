package com.sonozaki.services.domain.usecases

import com.sonozaki.entities.LogsData
import com.sonozaki.services.domain.repository.ServicesRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: ServicesRepository) {
    suspend operator fun invoke(): LogsData {
        return repository.getLogsData()
    }
}