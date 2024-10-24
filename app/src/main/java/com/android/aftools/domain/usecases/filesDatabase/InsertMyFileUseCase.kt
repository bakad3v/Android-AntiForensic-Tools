package com.android.aftools.domain.usecases.filesDatabase

import android.net.Uri
import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class InsertMyFileUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(uri: Uri, isDirectory: Boolean) {
    repository.insertMyFile(uri, isDirectory)
  }
}
