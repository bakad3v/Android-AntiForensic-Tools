package com.oasisfeng.island.domain.usecases.filesDatabase

import android.net.Uri
import com.oasisfeng.island.domain.repositories.FilesRepository
import javax.inject.Inject

class InsertMyFileUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(uri: Uri, isDirectory: Boolean) {
    repository.insertMyFile(uri, isDirectory)
  }
}
