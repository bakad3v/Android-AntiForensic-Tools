package com.android.aftools.data.dataMigrations

import android.content.Context
import androidx.datastore.core.DataMigration
import com.android.aftools.data.encryption.EncryptedSerializer
import com.android.aftools.data.mappers.SettingsVersionMapper
import com.android.aftools.datastoreDBA.dataStoreDirectBootAware
import com.android.aftools.datastoreDBA.dataStoreFileDBA
import com.android.aftools.domain.entities.Settings
import com.android.aftools.domain.entities.SettingsV1
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

    private val Context.oldDatastore by dataStoreDirectBootAware(OLD_SETTINGS, settingsSerializerV1)
    
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