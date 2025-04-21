package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.MultiuserUIProtection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceProtectionSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    permissionsSerializer: BaseSerializer<DeviceProtectionSettings>
) : DeviceProtectionSettingsRepository {

    private val Context.deviceProtectionSettingsDatastore by encryptedDataStore(
        DATASTORE_NAME,
        permissionsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    override val deviceProtectionSettings: Flow<DeviceProtectionSettings>
        get() = context.deviceProtectionSettingsDatastore.data

    override suspend fun changeRebootDelay(delay: Int) {
        context.deviceProtectionSettingsDatastore.updateData {
            it.copy(rebootDelay = delay)
        }
    }

    override suspend fun changeMultiuserUIProtection(multiuserUIProtection: MultiuserUIProtection) {
        context.deviceProtectionSettingsDatastore.updateData {
            it.copy(multiuserUIProtection = multiuserUIProtection)
        }
    }

    override suspend fun changeRebootOnLockStatus(status: Boolean) {
        context.deviceProtectionSettingsDatastore.updateData {
            it.copy(rebootOnLock = status)
        }
    }


    companion object {
        private const val DATASTORE_NAME = "device_protection_settings.json"
    }
}