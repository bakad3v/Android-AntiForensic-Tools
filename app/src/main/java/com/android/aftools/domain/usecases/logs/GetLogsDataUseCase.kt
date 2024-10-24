package com.android.aftools.domain.usecases.logs

import com.android.aftools.domain.repositories.LogsRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: LogsRepository) {
  operator fun invoke() = repository.getLogsData()

}
