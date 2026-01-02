package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.deviceProtectedDataStoreFile
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.entities.SettingsV1
import com.sonozaki.data.settings.mappers.SettingsVersionMapper
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 settings and v2 settings
 */
class SettingsMigrationV1 @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsSerializerV1: EncryptedSerializer<SettingsV1>,
    private val settingsVersionMapper: SettingsVersionMapper
) : DataMigration<Settings> {

    private val Context.oldDatastore by deviceProtectedDataStore(
        OLD_SETTINGS,
        settingsSerializerV1
    )
    
    override suspend fun cleanUp() {
        context.deviceProtectedDataStoreFile(OLD_SETTINGS).delete()
    }

    override suspend fun migrate(currentData: Settings): Settings {
        val oldData = context.oldDatastore.data.first()
        return settingsVersionMapper.mapOldSettingsToNew(oldData)
    }

    override suspend fun shouldMigrate(currentData: Settings): Boolean {
        return context.deviceProtectedDataStoreFile(OLD_SETTINGS).exists()
    }

    companion object {
        private const val OLD_SETTINGS = "settings_datastore.json"
    }
}