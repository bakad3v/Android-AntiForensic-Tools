package com.sonozaki.logs.domain.usecases

import com.sonozaki.logs.domain.repository.LogsScreenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableDaysUseCase @Inject constructor(private val repository: LogsScreenRepository) {
  operator fun invoke(): Flow<Set<Long>> {
    return repository.availableDays
  }
}
