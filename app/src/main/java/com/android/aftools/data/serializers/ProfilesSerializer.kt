package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.data.entities.IntList
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