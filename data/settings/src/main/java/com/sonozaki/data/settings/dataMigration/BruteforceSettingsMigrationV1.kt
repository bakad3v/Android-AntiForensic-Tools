package com.sonozaki.data.settings.dataMigration

import android.content.Context
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.datastore.dataStoreFile
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.data.settings.entities.BruteforceSettingsV1
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BruteforceSettingsMigrationV1  @Inject constructor(@ApplicationContext private val context: Context,
                                                         buttonSettingsSerializerV1: BaseSerializer<BruteforceSettingsV1>)
    : DataMigration<BruteforceSettings> {

    private val Context.oldDatastore by encryptedDataStore(
        OLD_BRUTEFORCE_SETTINGS,
        buttonSettingsSerializerV1,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    override suspend fun cleanUp() {
        context.dataStoreFile(OLD_BRUTEFORCE_SETTINGS, true).delete()
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
        return context.dataStoreFile(OLD_BRUTEFORCE_SETTINGS, true).exists()
    }

    companion object {
        private const val OLD_BRUTEFORCE_SETTINGS = "bruteforce_datastore.json"
    }
}