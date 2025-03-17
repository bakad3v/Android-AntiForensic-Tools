package com.sonozaki.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreFileDBA
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Settings
import com.sonozaki.settings.entities.SettingsV1
import com.sonozaki.settings.mappers.SettingsVersionMapper
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

    private val Context.oldDatastore by dataStoreDirectBootAware(
        OLD_SETTINGS,
        settingsSerializerV1
    )
    
    override suspend fun cleanUp() {
        context.dataStoreFileDBA(OLD_SETTINGS).delete()
    }

    override suspend fun migrate(currentData: Settings): Settings {
        val oldData = context.oldDatastore.data.first()
        return settingsVersionMapper.mapOldSettingsToNew(oldData)
    }

    override suspend fun shouldMigrate(currentData: Settings): Boolean {
        return context.dataStoreFileDBA(OLD_SETTINGS).exists()
    }

    companion object {
        private const val OLD_SETTINGS = "settings_datastore.json"
    }
}