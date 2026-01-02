package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.dataMigration.USBMigrationV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.UsbSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for changing settings of usb protection
 */
class USBSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    usbSettingsSerializer: EncryptedSerializer<UsbSettings>,
    private val usbMigrationV1: USBMigrationV1):
    UsbSettingsRepository {
    private val Context.usbDataStore by deviceProtectedDataStore(
        DATASTORE_NAME,
        produceMigrations = {
            listOf(usbMigrationV1)
        },
        serializer = usbSettingsSerializer
    )

    companion object {
        private const val DATASTORE_NAME = "usb_datastore_v2.json"
    }


    override val usbSettings: Flow<UsbSettings> = context.usbDataStore.data

    /**
     * Enable/disable usb protection
     */
    override suspend fun setUsbSettings(settings: UsbSettings) {
        context.usbDataStore.updateData {
            settings
        }
    }
}