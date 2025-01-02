package com.oasisfeng.island.domain.usecases.logs

import com.oasisfeng.island.domain.repositories.LogsRepository
import javax.inject.Inject

class ChangeLogsEnabledUseCase @Inject constructor(private val repository: LogsRepository) {
  suspend operator fun invoke() {
    repository.changeLogsEnabled()
  }
}
