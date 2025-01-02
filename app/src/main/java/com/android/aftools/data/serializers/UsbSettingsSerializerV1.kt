package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.domain.entities.UsbSettingsV1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UsbSettingsSerializerV1 @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<UsbSettingsV1> {
    override val defaultValue: UsbSettingsV1
        get() = UsbSettingsV1()

    override suspend fun readFrom(input: InputStream): UsbSettingsV1 {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = UsbSettingsV1.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UsbSettingsV1, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                    serializer = UsbSettingsV1.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }

}