package com.sonozaki.encrypteddatastore.encryption

import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class EncryptedSerializer<T> @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val dispatcherIO: CoroutineDispatcher,
    private val serializer: KSerializer<T>,
    override val defaultValue: T,
    private val alias: EncryptionAlias = EncryptionAlias.DATASTORE,
):
    Serializer<T> {
    override suspend fun readFrom(input: InputStream): T = withContext(dispatcherIO) {
        return@withContext try {
            val decryptedBytes = encryptionManager.decrypt(alias.name, input)
            Json.decodeFromString(
                deserializer = serializer,
                string = decryptedBytes.decodeToString()
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(dispatcherIO) {
            encryptionManager.encrypt(
                alias.name,
                Json.encodeToString(
                    serializer = serializer,
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }
}