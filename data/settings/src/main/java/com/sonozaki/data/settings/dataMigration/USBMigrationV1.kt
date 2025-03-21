package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreFileDBA
import com.sonozaki.data.settings.entities.UsbSettingsV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.UsbSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 USB settings and v2 settings
 */
class USBMigrationV1 @Inject constructor(
    @ApplicationContext private val context: Context,
    usbSettingsSerializerV1: BaseSerializer<UsbSettingsV1>
): DataMigration<UsbSettings> {

    private val Context.oldDatastore by encryptedDataStore(
        OLD_USB,
        usbSettingsSerializerV1,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )
    override suspend fun cleanUp() {
        context.dataStoreFileDBA(OLD_USB).delete()
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
        return context.dataStoreFileDBA(OLD_USB).exists()
    }

    companion object {
        private const val OLD_USB = "usb_datastore.json"
    }
}