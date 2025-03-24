package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import java.time.LocalDateTime
import javax.inject.Inject

class LookLogsForDayUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  suspend operator fun invoke(localDateTime: LocalDateTime) {
    repository.lookLogsForDay(localDateTime)
  }

}
