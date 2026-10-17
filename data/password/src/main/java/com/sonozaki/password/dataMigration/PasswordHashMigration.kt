package com.sonozaki.password.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.datastore.dataStoreFile
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.password.entities.PasswordStatusV1
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PasswordHashMigration @Inject constructor(
    @ApplicationContext private val context: Context,
    passwordStatusV1Serializer: BaseSerializer<PasswordStatusV1>,
    private val migrationMapper: PasswordHashMigrationMapper
) : DataMigration<PasswordStatus> {

    private val Context.oldPasswordPreferences by encryptedDataStore(
        OLD_PREFERENCES_NAME,
        passwordStatusV1Serializer,
        alias = EncryptionAlias.PASSWORD.name,
        isDBA = true
    )

    override suspend fun shouldMigrate(currentData: PasswordStatus): Boolean {
        return context.dataStoreFile(OLD_PREFERENCES_NAME, true).exists()
    }

    override suspend fun migrate(currentData: PasswordStatus): PasswordStatus {
        if (currentData.passwordSet && currentData.passwordHash != null) {
            return currentData
        }

        val oldPasswordStatus = context.oldPasswordPreferences.data.first()
        return migrationMapper(oldPasswordStatus)
    }

    override suspend fun cleanUp() {
        context.dataStoreFile(OLD_PREFERENCES_NAME, true).delete()
    }

    companion object {
        private const val OLD_PREFERENCES_NAME = "password_preferences"
    }
}
