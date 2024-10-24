package com.android.aftools.domain.usecases.filesDatabase
import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class ClearDbUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke() {
    repository.clearDb()
  }
}
