package com.android.aftools.domain.usecases.filesDatabase

import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.domain.repositories.FilesRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(private val repository: FilesRepository) {
  operator fun invoke(): StateFlow<FilesSortOrder> {
    return repository.getSortOrder()
  }
}
