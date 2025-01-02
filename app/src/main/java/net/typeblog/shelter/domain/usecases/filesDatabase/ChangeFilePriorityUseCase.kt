package net.typeblog.shelter.domain.usecases.filesDatabase

import android.net.Uri
import net.typeblog.shelter.domain.repositories.FilesRepository
import javax.inject.Inject

class ChangeFilePriorityUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(priority: Int, uri: Uri) {
    repository.changeFilePriority(priority, uri)
  }
}
