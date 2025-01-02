package net.typeblog.shelter.domain.usecases.logs

import net.typeblog.shelter.domain.repositories.LogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableDaysUseCase @Inject constructor(private val repository: LogsRepository) {
  operator fun invoke(): Flow<Set<Long>> {
    return repository.getAvailableDays()
  }
}
