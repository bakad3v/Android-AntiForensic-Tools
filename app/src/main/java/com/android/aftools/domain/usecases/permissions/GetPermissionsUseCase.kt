package com.android.aftools.domain.usecases.permissions

import com.android.aftools.domain.entities.Permissions
import com.android.aftools.domain.repositories.PermissionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(private val repository: PermissionsRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.permissions
    }
}