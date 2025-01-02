package net.typeblog.shelter.domain.usecases.permissions

import net.typeblog.shelter.domain.repositories.PermissionsRepository
import javax.inject.Inject

class SetRootActiveUseCase @Inject constructor(private val repository: PermissionsRepository) {
    suspend operator fun invoke(status: Boolean) {
        repository.setRootStatus(status)
    }
}
