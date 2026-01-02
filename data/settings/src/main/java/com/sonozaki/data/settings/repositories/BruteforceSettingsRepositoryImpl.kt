package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.dataMigration.BruteforceSettingsMigrationV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BruteforceSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    bruteforceSettingsSerializer: EncryptedSerializer<BruteforceSettings>,
    bruteforceSettingsMigrationV1: BruteforceSettingsMigrationV1):
    BruteforceRepository {

    private val Context.bruteforceDataStore by deviceProtectedDataStore(
        DATASTORE_NAME,
        bruteforceSettingsSerializer,
        produceMigrations = {
            listOf(bruteforceSettingsMigrationV1)
        }
    )

    companion object {
        private const val DATASTORE_NAME = "bruteforce_datastore_v2.json"
    }

    override val bruteforceSettings: Flow<BruteforceSettings> = context.bruteforceDataStore.data

    override suspend fun setBruteforceStatus(status: BruteforceDetectingMethod) {
        context.bruteforceDataStore.updateData {
            it.copy(detectingMethod = status)
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