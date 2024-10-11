package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.domain.entities.BruteforceSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class BruteforceSettingsSerializer @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<BruteforceSettings> {
    override val defaultValue: BruteforceSettings
        get() = BruteforceSettings()

    override suspend fun readFrom(input: InputStream): BruteforceSettings {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = BruteforceSettings.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: BruteforceSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                    serializer = BruteforceSettings.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }
}