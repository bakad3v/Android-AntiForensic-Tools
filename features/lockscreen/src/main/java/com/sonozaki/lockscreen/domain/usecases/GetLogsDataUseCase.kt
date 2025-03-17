package com.sonozaki.lockscreen.domain.usecases

import com.sonozaki.entities.LogsData
import com.sonozaki.lockscreen.domain.repository.LockScreenRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: LockScreenRepository) {
    suspend operator fun invoke(): LogsData {
        return repository.getLogsData()
    }
}