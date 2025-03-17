package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import javax.inject.Inject

class ChangeAutoDeletionTimeOutUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  suspend operator fun invoke(timeout: Int) {
    repository.changeAutoDeletionTimeOut(timeout)
  }
}
