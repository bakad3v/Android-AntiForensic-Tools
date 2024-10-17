package net.typeblog.shelter.domain.usecases.filesDatabase

import android.net.Uri
import net.typeblog.shelter.domain.repositories.FilesRepository
import javax.inject.Inject

class DeleteMyFileUseCase @Inject constructor(private val repository: FilesRepository) {
  suspend operator fun invoke(uri: Uri) {
    repository.deleteMyFile(uri)
  }
}
