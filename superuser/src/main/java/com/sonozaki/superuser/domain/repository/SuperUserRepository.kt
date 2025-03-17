package com.sonozaki.superuser.domain.repository

import com.sonozaki.entities.Permissions


interface SuperUserRepository {
    suspend fun getPermissions(): Permissions
    fun setRootInactive()
    fun setOwnerInactive()
    fun setAdminInactive()
}