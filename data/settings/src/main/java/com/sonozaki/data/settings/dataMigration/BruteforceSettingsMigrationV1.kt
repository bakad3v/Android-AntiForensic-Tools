package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.deviceProtectedDataStoreFile
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.entities.BruteforceSettingsV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BruteforceSettingsMigrationV1  @Inject constructor(@ApplicationContext private val context: Context,
                                                         buttonSettingsSerializerV1: EncryptedSerializer<BruteforceSettingsV1>)
    : DataMigration<BruteforceSettings> {

    private val Context.oldDatastore by deviceProtectedDataStore(
        OLD_BRUTEFORCE_SETTINGS,
        buttonSettingsSerializerV1
    )

    override suspend fun cleanUp() {
        context.deviceProtectedDataStoreFile(OLD_BRUTEFORCE_SETTINGS).delete()
    }

    override suspend fun migrate(currentData: BruteforceSettings): BruteforceSettings {
        val oldData = context.oldDatastore.data.first()
        val bruteforceDetectingMethod = if (oldData.bruteforceRestricted) {
            BruteforceDetectingMethod.ADMIN
        } else {
            BruteforceDetectingMethod.NONE
        }
        return BruteforceSettings(oldData.wrongAttempts, oldData.allowedAttempts, bruteforceDetectingMethod)
    }

    override suspend fun shouldMigrate(currentData: BruteforceSettings): Boolean {
        return context.deviceProtectedDataStoreFile(OLD_BRUTEFORCE_SETTINGS).exists()
    }

    companion object {
        private const val OLD_BRUTEFORCE_SETTINGS = "bruteforce_datastore.json"
    }
}