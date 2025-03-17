package com.sonozaki.superuser.domain.usecases


import com.sonozaki.entities.Permissions
import com.sonozaki.superuser.domain.repository.SuperUserRepository
import javax.inject.Inject

class GetPermissionsUseCase @Inject constructor(private val repository: SuperUserRepository) {
    suspend operator fun invoke(): Permissions {
        return repository.getPermissions()
    }
}