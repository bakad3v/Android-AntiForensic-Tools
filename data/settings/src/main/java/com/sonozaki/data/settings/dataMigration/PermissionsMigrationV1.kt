package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.datastore.dataStoreFile
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.settings.entities.PermissionsV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.Permissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PermissionsMigrationV1 @Inject constructor(@ApplicationContext private val context: Context,
                                                 permissionsSerializer: BaseSerializer<PermissionsV1>)
    : DataMigration<Permissions> {

    private val Context.oldDatastore by encryptedDataStore(
        OLD_PERMISSIONS,
        permissionsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    override suspend fun cleanUp() {
        context.dataStoreFile(OLD_PERMISSIONS, true).delete()
    }

    override suspend fun migrate(currentData: Permissions): Permissions {
        val oldData = context.oldDatastore.data.first()
        return Permissions(oldData.isAdmin, oldData.isOwner, oldData.isRoot)
    }

    override suspend fun shouldMigrate(currentData: Permissions): Boolean {
        return context.dataStoreFile(OLD_PERMISSIONS, true).exists()
    }

    companion object {
        private const val OLD_PERMISSIONS = "permissions_datastore.json"
    }
}