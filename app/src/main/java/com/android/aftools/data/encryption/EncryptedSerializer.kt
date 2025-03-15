package com.android.aftools.data.encryption

import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializer for datastore with encryption.
 *
 * Why do I use encrypted DataStore instead of encrypted database for storing lists of items? SQLCypher, the library for database encryption, encrypts and decrypts data using encryption key stored in the RAM. On some devices it's possible to dump RAM without the root rights and extract the encryption key, as demonstrated in this [post](https://cellebrite.com/en/decrypting-databases-using-ram-dump-health-data/) by the Cellebrite. On the other hand, in this app data is encrypted and decrypted in Android KeyStore, isolated environment where cryptographic operations may be executed safely and keys can't be extracted. It's easier to implement such an encryption with Datastore than with database.
 */
open class EncryptedSerializer<T>(private val encryptionManager: EncryptionManager, private val dispatcherIO: CoroutineDispatcher, private val serializer: KSerializer<T>, override val defaultValue: T, private val alias: EncryptionAlias = EncryptionAlias.DATASTORE):
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