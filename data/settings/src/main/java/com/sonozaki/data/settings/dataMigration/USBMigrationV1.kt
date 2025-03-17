package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreFileDBA
import com.sonozaki.data.settings.entities.UsbSettingsV1
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 USB settings and v2 settings
 */
class USBMigrationV1 @Inject constructor(@ApplicationContext private val context: Context, usbSettingsSerializerV1: com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer<UsbSettingsV1>): DataMigration<com.sonozaki.entities.UsbSettings> {

    private val Context.oldDatastore by com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware(
        OLD_USB,
        usbSettingsSerializerV1
    )
    override suspend fun cleanUp() {
        context.dataStoreFileDBA(OLD_USB).delete()
    }

    override suspend fun migrate(currentData: com.sonozaki.entities.UsbSettings): com.sonozaki.entities.UsbSettings {
        val oldData = context.oldDatastore.data.first()
        return if (oldData.runOnConnection)  {
            com.sonozaki.entities.UsbSettings.RUN_ON_CONNECTION
        } else {
            com.sonozaki.entities.UsbSettings.DO_NOTHING
        }
    }

    override suspend fun shouldMigrate(currentData: com.sonozaki.entities.UsbSettings): Boolean {
        return context.dataStoreFileDBA(OLD_USB).exists()
    }

    companion object {
        private const val OLD_USB = "usb_datastore.json"
    }
}