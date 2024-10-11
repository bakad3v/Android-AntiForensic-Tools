package com.android.aftools.domain.usecases.logs

import com.android.aftools.domain.repositories.LogsRepository
import javax.inject.Inject

class ClearLogsForDayUseCase @Inject constructor(private val repository: LogsRepository) {
  suspend operator fun invoke(day: String) {
    repository.clearLogsForDay(day)
  }
}
