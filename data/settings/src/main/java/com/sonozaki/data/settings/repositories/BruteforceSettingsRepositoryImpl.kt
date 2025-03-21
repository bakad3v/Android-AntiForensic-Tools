package com.sonozaki.data.settings.repositories

import android.content.Context
import com.sonozaki.bedatastore.datastore.encryptedDataStore
import com.sonozaki.encrypteddatastore.BaseSerializer
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.encrypteddatastore.datastoreDBA.dataStoreDirectBootAware
import com.sonozaki.encrypteddatastore.encryption.EncryptionAlias
import com.sonozaki.entities.BruteforceSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BruteforceSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    bruteforceSettingsSerializer: BaseSerializer<BruteforceSettings>):
    BruteforceRepository {

    private val Context.bruteforceDataStore by encryptedDataStore(
        DATASTORE_NAME,
        bruteforceSettingsSerializer,
        alias = EncryptionAlias.DATASTORE.name,
        isDBA = true
    )

    companion object {
        private const val DATASTORE_NAME = "bruteforce_datastore.json"
    }

    override val bruteforceSettings: Flow<BruteforceSettings> = context.bruteforceDataStore.data

    override suspend fun setBruteforceStatus(status: Boolean) {
        context.bruteforceDataStore.updateData {
            it.copy(bruteforceRestricted = status)
        }
    }

    override suspend fun setBruteforceLimit(limit: Int) {
        context.bruteforceDataStore.updateData {
            it.copy(allowedAttempts = limit)
        }
    }

    override suspend fun onWrongPassword(): Boolean {
        context.bruteforceDataStore.updateData {
            it.copy(wrongAttempts = it.wrongAttempts+1)
        }
        val data = context.bruteforceDataStore.data.first()
        return data.wrongAttempts >= data.allowedAttempts
    }

    override suspend fun onRightPassword() {
        context.bruteforceDataStore.updateData {
            it.copy(wrongAttempts = 0)
        }
    }

}