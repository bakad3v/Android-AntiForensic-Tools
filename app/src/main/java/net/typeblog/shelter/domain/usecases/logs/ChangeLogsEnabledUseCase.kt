package net.typeblog.shelter.domain.usecases.logs

import net.typeblog.shelter.domain.repositories.LogsRepository
import javax.inject.Inject

class ChangeLogsEnabledUseCase @Inject constructor(private val repository: LogsRepository) {
  suspend operator fun invoke() {
    repository.changeLogsEnabled()
  }
}
