package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.domain.entities.PasswordStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class PasswordStatusSerializer @Inject constructor(private val encryptionManager: EncryptionManager): Serializer<PasswordStatus> {
    override val defaultValue: PasswordStatus
        get() = PasswordStatus()

    override suspend fun readFrom(input: InputStream): PasswordStatus {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.PASSWORD.name,input)
        return try {
            Json.decodeFromString(deserializer = PasswordStatus.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: PasswordStatus, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.PASSWORD.name,
                Json.encodeToString(
                    serializer = PasswordStatus.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }

}