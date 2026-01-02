package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.dataMigration.PermissionsMigrationV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Permissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PermissionsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    permissionsSerializer: EncryptedSerializer<Permissions>,
    permissionsMigrationV1: PermissionsMigrationV1):
    PermissionsRepository {
    private val Context.permissionsDatastore by deviceProtectedDataStore(
        DATASTORE_NAME,
        produceMigrations = {
            listOf(permissionsMigrationV1)
        },
        serializer = permissionsSerializer
    )

    companion object {
        private const val DATASTORE_NAME = "permissions_datastore_v2.json"
    }

    override val permissions: Flow<Permissions> = context.permissionsDatastore.data

    override suspend fun setAdminStatus(status: Boolean) {
        context.permissionsDatastore.updateData {
            it.copy(isAdmin = status)
        }
    }

    override suspend fun setOwnerStatus(status: Boolean) {
        context.permissionsDatastore.updateData {
            it.copy(isOwner = status)
        }
    }

    override suspend fun setRootStatus(status: Boolean) {
        context.permissionsDatastore.updateData {
            it.copy(isRoot = status)
        }
    }

    override suspend fun setShizukuPermission(permission: Boolean) {
        context.permissionsDatastore.updateData {
            it.copy(isShizuku = permission)
        }
    }
}