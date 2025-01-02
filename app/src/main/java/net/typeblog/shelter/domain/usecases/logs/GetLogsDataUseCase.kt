package net.typeblog.shelter.domain.usecases.logs

import net.typeblog.shelter.domain.repositories.LogsRepository
import javax.inject.Inject

class GetLogsDataUseCase @Inject constructor(private val repository: LogsRepository) {
  operator fun invoke() = repository.getLogsData()

}
