package net.typeblog.shelter.domain.usecases.filesDatabase

import net.typeblog.shelter.domain.repositories.FilesRepository
import javax.inject.Inject

class GetFilesDbUseCase @Inject constructor(private val repository: FilesRepository){
  operator fun invoke() = repository.getFilesDb()
}
