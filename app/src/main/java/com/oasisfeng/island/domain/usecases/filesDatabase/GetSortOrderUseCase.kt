package com.oasisfeng.island.domain.usecases.filesDatabase

import com.oasisfeng.island.domain.entities.FilesSortOrder
import com.oasisfeng.island.domain.repositories.FilesRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(private val repository: FilesRepository) {
  operator fun invoke(): StateFlow<FilesSortOrder> {
    return repository.getSortOrder()
  }
}
