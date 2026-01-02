package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.deviceProtectedDataStoreFile
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.entities.ButtonSettingsV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.PowerButtonTriggerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ButtonSettingsMigrationV1 @Inject constructor(@ApplicationContext private val context: Context,
                                                    buttonSettingsSerializerV1: EncryptedSerializer<ButtonSettingsV1>)
    : DataMigration<ButtonSettings> {

    private val Context.oldDatastore by deviceProtectedDataStore(
        OLD_BUTTON_SETTINGS,
        buttonSettingsSerializerV1
    )

    override suspend fun cleanUp() {
        context.deviceProtectedDataStoreFile(OLD_BUTTON_SETTINGS).delete()
    }

    override suspend fun migrate(currentData: ButtonSettings): ButtonSettings {
        val oldData = context.oldDatastore.data.first()
        val oldTriggerOption = if (oldData.triggerOnButton) {
            PowerButtonTriggerOptions.DEPRECATED_WAY
        } else {
            PowerButtonTriggerOptions.IGNORE
        }
        return ButtonSettings(oldTriggerOption, oldData.latency, oldData.allowedClicks)
    }

    override suspend fun shouldMigrate(currentData: ButtonSettings): Boolean {
        return context.deviceProtectedDataStoreFile(OLD_BUTTON_SETTINGS).exists()
    }

    companion object {
        private const val OLD_BUTTON_SETTINGS = "button_datastore.json"
    }
}