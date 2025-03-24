package com.sonozaki.files.domain.usecases

import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class SetDeleteFilesUseCase @Inject constructor(private val repository: FilesScreenRepository) {
    suspend operator fun invoke(new: Boolean) {
        repository.setDeleteFiles(new)
    }
}

