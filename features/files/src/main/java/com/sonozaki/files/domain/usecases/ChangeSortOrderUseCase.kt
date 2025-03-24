package com.sonozaki.files.domain.usecases

import com.sonozaki.files.domain.repository.FilesScreenRepository
import javax.inject.Inject

class ChangeSortOrderUseCase @Inject constructor(private val repository: FilesScreenRepository){
  suspend operator fun invoke(sortOrder: com.sonozaki.entities.FilesSortOrder) {
    repository.changeSortOrder(sortOrder)
  }
}
