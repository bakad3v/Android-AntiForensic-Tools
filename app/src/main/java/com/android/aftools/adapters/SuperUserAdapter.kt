package com.android.aftools.adapters

import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.entities.Permissions
import com.sonozaki.superuser.domain.repository.SuperUserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SuperUserAdapter @Inject constructor(private val permissionsRepository: PermissionsRepository): SuperUserRepository {
    override suspend fun getPermissions(): Permissions {
        return permissionsRepository.permissions.first()
    }

    override suspend fun setRootInactive() {
        permissionsRepository.setRootStatus(false)
    }

    override suspend fun setOwnerInactive() {
        permissionsRepository.setOwnerStatus(false)
    }

    override suspend fun setAdminInactive() {
        permissionsRepository.setAdminStatus(false)
    }
}