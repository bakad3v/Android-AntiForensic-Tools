package com.android.aftools.domain.usecases.filesDatabase

import android.net.Uri
import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class ChangeFilePriorityUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(priority: Int, uri: Uri) {
    repository.changeFilePriority(priority, uri)
  }
}
