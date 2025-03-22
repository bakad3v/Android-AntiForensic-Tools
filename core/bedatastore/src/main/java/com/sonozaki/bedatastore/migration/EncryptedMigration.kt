package com.sonozaki.bedatastore.migration

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.encryption.EncryptedSerializer
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class EncryptedMigration<T> internal constructor(
    private val baseMigration: DataMigration<T>,
    private val encryptedSerializer: EncryptedSerializer<T>, ): DataMigration<ByteArray> {
    override suspend fun cleanUp() = baseMigration.cleanUp()


    override suspend fun shouldMigrate(currentData: ByteArray): Boolean {
        val currentDataDecrypted = encryptedSerializer.decryptAndDeserialize(currentData)
        return baseMigration.shouldMigrate(currentDataDecrypted)
    }

    override suspend fun migrate(currentData: ByteArray): ByteArray {
        Log.w("lifecycle","migrationStart")
        val currentDataDecrypted = encryptedSerializer.decryptAndDeserialize(currentData)
        val newData = baseMigration.migrate(currentDataDecrypted)
        Log.w("lifecycle","migrationEnd")
        return encryptedSerializer.serializeAndEncrypt(newData)
    }
}

internal fun <T> getEncryptedMigrations(context: Context, produceMigrations: (Context) -> List<DataMigration<T>>, encryptedSerializer: EncryptedSerializer<T>): List<EncryptedMigration<T>> =
    produceMigrations(context).map { EncryptedMigration(it, encryptedSerializer) }
