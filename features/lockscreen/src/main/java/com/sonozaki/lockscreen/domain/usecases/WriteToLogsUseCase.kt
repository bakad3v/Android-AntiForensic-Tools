package com.sonozaki.lockscreen.domain.usecases

import com.sonozaki.lockscreen.domain.repository.LockScreenRepository
import javax.inject.Inject

class WriteToLogsUseCase @Inject constructor(private val repository: LockScreenRepository) {
    suspend operator fun invoke(text: String) {
        repository.writeToLogs(text)
    }
}