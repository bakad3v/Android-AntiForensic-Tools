package com.sonozaki.files.domain.usecases

import android.net.Uri
import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class InsertMyFileUseCase @Inject constructor(private val repository: FilesScreenRepository) {
  suspend operator fun invoke(uri: Uri, isDirectory: Boolean) {
    repository.insertMyFile(uri, isDirectory)
  }
}
