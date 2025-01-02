package net.typeblog.shelter.data.repositories

import android.content.Context
import net.typeblog.shelter.data.dataMigrations.USBMigrationV1
import net.typeblog.shelter.data.serializers.UsbSettingsSerializer
import net.typeblog.shelter.datastoreDBA.dataStoreDirectBootAware
import net.typeblog.shelter.domain.entities.UsbSettings
import net.typeblog.shelter.domain.repositories.UsbSettingsRepository
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