package net.typeblog.shelter.data.serializers

import androidx.datastore.core.Serializer
import net.typeblog.shelter.data.encryption.EncryptionAlias
import net.typeblog.shelter.data.encryption.EncryptionManager
import net.typeblog.shelter.domain.entities.ButtonSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class ButtonSettingsSerializer @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<ButtonSettings> {
    override val defaultValue: ButtonSettings
        get() = ButtonSettings()

    override suspend fun readFrom(input: InputStream): ButtonSettings {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = ButtonSettings.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: ButtonSettings,
        output: OutputStream
    ) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                    serializer = ButtonSettings.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }

}