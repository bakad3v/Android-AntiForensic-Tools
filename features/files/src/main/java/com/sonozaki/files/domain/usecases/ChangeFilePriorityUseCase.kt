package com.sonozaki.files.domain.usecases

import android.net.Uri
import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class ChangeFilePriorityUseCase @Inject constructor(private val repository: FilesScreenRepository) {
  suspend operator fun invoke(priority: Int, uri: Uri) {
    repository.changeFilePriority(priority, uri)
  }
}
