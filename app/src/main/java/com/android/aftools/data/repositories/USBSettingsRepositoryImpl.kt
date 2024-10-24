package com.android.aftools.data.repositories

import android.content.Context
import com.android.aftools.data.serializers.UsbSettingsSerializer
import com.android.aftools.datastoreDBA.dataStoreDirectBootAware
import com.android.aftools.domain.entities.UsbSettings
import com.android.aftools.domain.repositories.UsbSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for changing settings of usb protection
 */
class USBSettingsRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context, usbSettingsSerializer: UsbSettingsSerializer):
    UsbSettingsRepository {
    private val Context.usbDataStore by dataStoreDirectBootAware(
        DATASTORE_NAME,
        usbSettingsSerializer
    )

    companion object {
        private const val DATASTORE_NAME = "usb_datastore.json"
    }


    override val usbSettings: Flow<UsbSettings> = context.usbDataStore.data

    /**
     * Enable/disable usb protection
     */
    override suspend fun setUsbConnectionStatus(status: Boolean) {
        context.usbDataStore.updateData {
            it.copy(runOnConnection = status)
        }
    }
}