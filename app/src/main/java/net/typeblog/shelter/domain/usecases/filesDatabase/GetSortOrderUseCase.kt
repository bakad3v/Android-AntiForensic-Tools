package net.typeblog.shelter.domain.usecases.filesDatabase

import net.typeblog.shelter.domain.entities.FilesSortOrder
import net.typeblog.shelter.domain.repositories.FilesRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(private val repository: FilesRepository) {
  operator fun invoke(): StateFlow<FilesSortOrder> {
    return repository.getSortOrder()
  }
}
