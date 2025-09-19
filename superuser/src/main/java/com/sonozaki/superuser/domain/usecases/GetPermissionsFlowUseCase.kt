package com.sonozaki.superuser.domain.usecases

import com.sonozaki.entities.Permissions
import com.sonozaki.superuser.domain.repository.SuperUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPermissionsFlowUseCase @Inject constructor(private val repository: SuperUserRepository) {
    operator fun invoke(): Flow<Permissions> {
        return repository.getPermissionsFlow()
    }
}