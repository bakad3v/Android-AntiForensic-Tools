package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.deviceProtectedDataStoreFile
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.entities.UsbSettingsV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.UsbSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 USB settings and v2 settings
 */
class USBMigrationV1 @Inject constructor(
    @ApplicationContext private val context: Context,
    usbSettingsSerializerV1: EncryptedSerializer<UsbSettingsV1>
): DataMigration<UsbSettings> {

    private val Context.oldDatastore by deviceProtectedDataStore(
        OLD_USB,
        usbSettingsSerializerV1
    )
    override suspend fun cleanUp() {
        context.deviceProtectedDataStoreFile(OLD_USB).delete()
    }

    override suspend fun migrate(currentData: UsbSettings): UsbSettings {
        val oldData = context.oldDatastore.data.first()
        return if (oldData.runOnConnection)  {
            UsbSettings.RUN_ON_CONNECTION
        } else {
            UsbSettings.DO_NOTHING
        }
    }

    override suspend fun shouldMigrate(currentData: UsbSettings): Boolean {
        return context.deviceProtectedDataStoreFile(OLD_USB).exists()
    }

    companion object {
        private const val OLD_USB = "usb_datastore.json"
    }
}