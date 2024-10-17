package net.typeblog.shelter.domain.usecases.logs

import net.typeblog.shelter.domain.repositories.LogsRepository
import javax.inject.Inject

class ChangeAutoDeletionTimeOutUseCase @Inject constructor(private val repository: LogsRepository) {
  suspend operator fun invoke(timeout: Int) {
    repository.changeAutoDeletionTimeOut(timeout)
  }
}
