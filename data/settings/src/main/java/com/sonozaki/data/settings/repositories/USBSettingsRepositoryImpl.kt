package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.settings.dataMigration.USBMigrationV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.UsbSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for changing settings of usb protection
 */
class USBSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    usbSettingsSerializer: BaseSerializer<UsbSettings>,
    private val usbMigrationV1: USBMigrationV1):
    UsbSettingsRepository {
    private val Context.usbDataStore by encryptedDataStore(
        DATASTORE_NAME,
        produceMigrations = { context ->
            listOf<USBMigrationV1>(usbMigrationV1)
        },
        serializer = usbSettingsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
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