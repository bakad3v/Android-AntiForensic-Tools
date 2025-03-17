package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  operator fun invoke() = repository.logsData

}
