package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.deviceProtectedDataStoreFile
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.entities.PermissionsV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Permissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PermissionsMigrationV1 @Inject constructor(@ApplicationContext private val context: Context,
                                                 permissionsSerializer: EncryptedSerializer<PermissionsV1>)
    : DataMigration<Permissions> {

    private val Context.oldDatastore by deviceProtectedDataStore(
        OLD_PERMISSIONS,
        permissionsSerializer
    )

    override suspend fun cleanUp() {
        context.deviceProtectedDataStoreFile(OLD_PERMISSIONS).delete()
    }

    override suspend fun migrate(currentData: Permissions): Permissions {
        val oldData = context.oldDatastore.data.first()
        return Permissions(oldData.isAdmin, oldData.isOwner, oldData.isRoot)
    }

    override suspend fun shouldMigrate(currentData: Permissions): Boolean {
        return context.deviceProtectedDataStoreFile(OLD_PERMISSIONS).exists()
    }

    companion object {
        private const val OLD_PERMISSIONS = "permissions_datastore.json"
    }
}