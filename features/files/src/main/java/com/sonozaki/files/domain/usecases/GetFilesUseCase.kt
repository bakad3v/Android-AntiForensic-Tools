package com.sonozaki.files.domain.usecases

import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(private val repository: FilesScreenRepository){
  operator fun invoke() = repository.files
}
