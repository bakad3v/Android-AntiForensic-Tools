package net.typeblog.shelter.domain.usecases.filesDatabase
import net.typeblog.shelter.domain.repositories.FilesRepository
import javax.inject.Inject

class ClearDbUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke() {
    repository.clearDb()
  }
}
