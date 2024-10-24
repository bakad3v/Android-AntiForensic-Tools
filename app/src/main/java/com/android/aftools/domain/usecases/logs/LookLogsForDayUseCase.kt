package com.android.aftools.domain.usecases.logs

import com.android.aftools.domain.repositories.LogsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class LookLogsForDayUseCase @Inject constructor(private val repository: LogsRepository) {
  suspend operator fun invoke(localDateTime: LocalDateTime) {
    repository.lookLogsForDay(localDateTime)
  }

}
