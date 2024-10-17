package net.typeblog.shelter.data.serializers

import androidx.datastore.core.Serializer
import net.typeblog.shelter.data.encryption.EncryptionAlias
import net.typeblog.shelter.data.encryption.EncryptionManager
import net.typeblog.shelter.data.entities.IntList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class ProfilesSerializer @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<IntList> {

    override val defaultValue: IntList
        get() = IntList()


    override suspend fun readFrom(input: InputStream): IntList {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = IntList.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: IntList, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                    serializer = IntList.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }


}