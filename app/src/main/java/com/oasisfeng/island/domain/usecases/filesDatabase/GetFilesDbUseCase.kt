package com.oasisfeng.island.domain.usecases.filesDatabase

import com.oasisfeng.island.domain.repositories.FilesRepository
import javax.inject.Inject

class GetFilesDbUseCase @Inject constructor(private val repository: FilesRepository){
  operator fun invoke() = repository.getFilesDb()
}
