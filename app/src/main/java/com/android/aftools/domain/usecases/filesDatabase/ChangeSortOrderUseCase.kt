package com.android.aftools.domain.usecases.filesDatabase

import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.domain.repositories.FilesRepository
import javax.inject.Inject

class ChangeSortOrderUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke(sortOrder: FilesSortOrder) {
    repository.changeSortOrder(sortOrder)
  }
}
