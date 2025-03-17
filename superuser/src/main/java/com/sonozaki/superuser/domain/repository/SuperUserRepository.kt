package com.sonozaki.superuser.domain.repository

import com.sonozaki.entities.Permissions


interface SuperUserRepository {
    suspend fun getPermissions(): Permissions
    suspend fun setRootInactive()
    suspend fun setOwnerInactive()
    suspend fun setAdminInactive()
}