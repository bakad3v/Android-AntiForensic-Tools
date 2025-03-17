package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import javax.inject.Inject

class ClearLogsForDayUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  suspend operator fun invoke(day: String) {
    repository.clearLogsForDay(day)
  }
}
