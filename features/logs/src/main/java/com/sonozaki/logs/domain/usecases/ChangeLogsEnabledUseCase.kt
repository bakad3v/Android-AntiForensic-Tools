package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import javax.inject.Inject

class ChangeLogsEnabledUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  suspend operator fun invoke() {
    repository.changeLogsEnabled()
  }
}
