package com.oasisfeng.island.domain.usecases.filesDatabase

import com.oasisfeng.island.domain.entities.FilesSortOrder
import com.oasisfeng.island.domain.repositories.FilesRepository
import javax.inject.Inject

class ChangeSortOrderUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke(sortOrder: FilesSortOrder) {
    repository.changeSortOrder(sortOrder)
  }
}
