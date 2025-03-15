package com.android.aftools.data.dataMigrations

import android.content.Context
import androidx.datastore.core.DataMigration
import com.android.aftools.data.encryption.EncryptedSerializer
import com.android.aftools.datastoreDBA.dataStoreDirectBootAware
import com.android.aftools.datastoreDBA.dataStoreFileDBA
import com.android.aftools.domain.entities.UsbSettings
import com.android.aftools.domain.entities.UsbSettingsV1
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Migration between v1 USB settings and v2 settings
 */
class USBMigrationV1 @Inject constructor(@ApplicationContext private val context: Context, usbSettingsSerializerV1: EncryptedSerializer<UsbSettingsV1>): DataMigration<UsbSettings> {

    private val Context.oldDatastore by dataStoreDirectBootAware(OLD_USB, usbSettingsSerializerV1)
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