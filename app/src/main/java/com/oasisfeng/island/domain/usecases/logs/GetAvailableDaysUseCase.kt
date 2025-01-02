package com.oasisfeng.island.domain.usecases.logs

import com.oasisfeng.island.domain.repositories.LogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableDaysUseCase @Inject constructor(private val repository: LogsRepository) {
  operator fun invoke(): Flow<Set<Long>> {
    return repository.getAvailableDays()
  }
}
