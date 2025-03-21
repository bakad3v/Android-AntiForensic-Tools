package com.sonozaki.bedatastore.migration

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataMigration
import com.sonozaki.bedatastore.encryption.EncryptedSerializer
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class EncryptedMigration<T> internal constructor(
    private val baseMigration: DataMigration<T>,
    private val encryptedSerializer: EncryptedSerializer<T>,
    private val coroutineContext: CoroutineContext): DataMigration<ByteArray> {
    override suspend fun cleanUp() = withContext(coroutineContext) {
        baseMigration.cleanUp()
    }

    override suspend fun shouldMigrate(currentData: ByteArray): Boolean = withContext(coroutineContext) {
        Log.w("input","baseMigrate")
        val currentDataDecrypted = encryptedSerializer.decryptAndDeserialize(currentData)
        return@withContext baseMigration.shouldMigrate(currentDataDecrypted)
    }

    override suspend fun migrate(currentData: ByteArray): ByteArray = withContext(coroutineContext) {
        Log.w("lifecycle","migrationStart")
        val currentDataDecrypted = encryptedSerializer.decryptAndDeserialize(currentData)
        val newData = baseMigration.migrate(currentDataDecrypted)
        Log.w("lifecycle","migrationEnd")
        return@withContext encryptedSerializer.serializeAndEncrypt(newData)
    }
}

internal fun <T> getEncryptedMigrations(context: Context, produceMigrations: (Context) -> List<DataMigration<T>>, encryptedSerializer: EncryptedSerializer<T>, coroutineContext: CoroutineContext): List<EncryptedMigration<T>> =
    produceMigrations(context).map { EncryptedMigration(it, encryptedSerializer, coroutineContext) }
