package com.sonozaki.data.settings.dataMigration

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataMigration
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.sonozaki.bedatastore.datastore.dataStoreFile
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreFileDBA
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Settings
import com.sonozaki.data.settings.entities.SettingsV1
import com.sonozaki.data.settings.mappers.SettingsVersionMapper
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 settings and v2 settings
 */
class SettingsMigrationV1 @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsSerializerV1: BaseSerializer<SettingsV1>,
    private val settingsVersionMapper: SettingsVersionMapper
) : DataMigration<Settings> {

    private val Context.oldDatastore by encryptedDataStore(
        OLD_SETTINGS,
        settingsSerializerV1,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )
    
    override suspend fun cleanUp() {
        context.dataStoreFile(OLD_SETTINGS, true).delete()
    }

    override suspend fun migrate(currentData: Settings): Settings {
        val oldData = context.oldDatastore.data.first()
        return settingsVersionMapper.mapOldSettingsToNew(oldData)
    }

    override suspend fun shouldMigrate(currentData: Settings): Boolean {
        Log.w("input",context.dataStoreFile(OLD_SETTINGS, true).exists().toString() + "migrate")
        return context.dataStoreFile(OLD_SETTINGS, true).exists()
    }

    companion object {
        private const val OLD_SETTINGS = "settings_datastore.json"
    }
}