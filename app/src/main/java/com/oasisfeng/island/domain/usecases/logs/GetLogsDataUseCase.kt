package com.oasisfeng.island.domain.usecases.logs

import com.oasisfeng.island.domain.repositories.LogsRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: LogsRepository) {
  operator fun invoke() = repository.getLogsData()

}
