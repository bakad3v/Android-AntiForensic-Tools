package com.oasisfeng.island.domain.usecases.filesDatabase
import com.oasisfeng.island.domain.repositories.FilesRepository
import javax.inject.Inject

class ClearDbUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke() {
    repository.clearDb()
  }
}
