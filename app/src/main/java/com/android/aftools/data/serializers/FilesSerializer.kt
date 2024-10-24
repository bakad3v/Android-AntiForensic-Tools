package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.data.entities.FilesList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class FilesSerializer @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<FilesList> {

    override val defaultValue: FilesList
        get() = FilesList()


    override suspend fun readFrom(input: InputStream): FilesList {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = FilesList.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: FilesList, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                   serializer = FilesList.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }


}