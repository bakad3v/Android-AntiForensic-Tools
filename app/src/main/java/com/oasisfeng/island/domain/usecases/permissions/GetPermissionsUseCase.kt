package com.oasisfeng.island.domain.usecases.permissions

import com.oasisfeng.island.domain.entities.Permissions
import com.oasisfeng.island.domain.repositories.PermissionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(private val repository: PermissionsRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.permissions
    }
}