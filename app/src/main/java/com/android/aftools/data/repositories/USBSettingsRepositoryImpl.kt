package com.android.aftools.data.repositories

import android.content.Context
import com.android.aftools.data.dataMigrations.USBMigrationV1
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
class USBSettingsRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context, usbSettingsSerializer: UsbSettingsSerializer, private val usbMigrationV1: USBMigrationV1):
    UsbSettingsRepository {
    private val Context.usbDataStore by dataStoreDirectBootAware(
        DATASTORE_NAME,
        produceMigrations = {
            context -> listOf<USBMigrationV1>(usbMigrationV1)
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