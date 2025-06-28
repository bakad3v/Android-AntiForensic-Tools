package com.sonozaki.superuser.domain.repository

import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow


interface SuperUserRepository {
    suspend fun getPermissions(): Permissions
    fun getPermissionsFlow(): Flow<Permissions>
    suspend fun setShizukuPermission(active: Boolean)
    suspend fun setRootInactive()
    suspend fun setOwnerInactive()
    suspend fun setAdminInactive()
}