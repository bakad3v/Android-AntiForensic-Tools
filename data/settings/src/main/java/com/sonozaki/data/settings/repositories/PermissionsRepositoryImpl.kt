package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.settings.dataMigration.PermissionsMigrationV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.Permissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PermissionsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    permissionsSerializer: BaseSerializer<Permissions>,
    permissionsMigrationV1: PermissionsMigrationV1):
    PermissionsRepository {
    private val Context.permissionsDatastore by encryptedDataStore(
        DATASTORE_NAME,
        produceMigrations = { context ->
            listOf<PermissionsMigrationV1>(permissionsMigrationV1)
        },
        serializer = permissionsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
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