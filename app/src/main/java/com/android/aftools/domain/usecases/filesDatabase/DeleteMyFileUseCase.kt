package com.android.aftools.domain.usecases.filesDatabase

import android.net.Uri
import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class DeleteMyFileUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(uri: Uri) {
    repository.deleteMyFile(uri)
  }
}
