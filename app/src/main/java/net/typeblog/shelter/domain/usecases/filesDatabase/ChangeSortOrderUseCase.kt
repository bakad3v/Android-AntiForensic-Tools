package net.typeblog.shelter.domain.usecases.filesDatabase

import net.typeblog.shelter.domain.entities.FilesSortOrder
import net.typeblog.shelter.domain.repositories.FilesRepository
import javax.inject.Inject

class ChangeSortOrderUseCase @Inject constructor(private val repository: FilesRepository){
  suspend operator fun invoke(sortOrder: FilesSortOrder) {
    repository.changeSortOrder(sortOrder)
  }
}
