package com.android.aftools.domain.usecases.filesDatabase

import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class GetFilesDbUseCase @Inject constructor(private val repository: FilesRepository){
  operator fun invoke() = repository.getFilesDb()
}
