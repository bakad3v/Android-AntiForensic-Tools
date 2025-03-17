package com.sonozaki.files.domain.usecases

import com.sonozaki.entities.FilesSortOrder
import com.sonozaki.files.domain.repository.FilesScreenRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(private val repository: FilesScreenRepository) {
  operator fun invoke(): StateFlow<FilesSortOrder> {
    return repository.sortOrder
  }
}
