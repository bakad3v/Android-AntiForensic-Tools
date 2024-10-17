package net.typeblog.shelter.domain.usecases.permissions

import net.typeblog.shelter.domain.entities.Permissions
import net.typeblog.shelter.domain.repositories.PermissionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(private val repository: PermissionsRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.permissions
    }
}