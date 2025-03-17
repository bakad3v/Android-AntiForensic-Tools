package com.sonozaki.files.domain.usecases

import android.net.Uri
import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class DeleteMyFileUseCase @Inject constructor(private val repository: FilesScreenRepository) {
  suspend operator fun invoke(uri: Uri) {
    repository.deleteMyFile(uri)
  }
}
