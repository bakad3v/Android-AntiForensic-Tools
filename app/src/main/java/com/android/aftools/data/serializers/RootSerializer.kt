package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.domain.entities.RootDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class RootSerializer @Inject constructor(private val encryptionManager: EncryptionManager):
    Serializer<RootDomain> {

    override val defaultValue: RootDomain
        get() = RootDomain()


    override suspend fun readFrom(input: InputStream): RootDomain {
        val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
        return try {
            Json.decodeFromString(deserializer = RootDomain.serializer(),
                string = decryptedBytes.decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: RootDomain, output: OutputStream) {
        withContext(Dispatchers.IO) {
            encryptionManager.encrypt(
                EncryptionAlias.DATASTORE.name,
                Json.encodeToString(
                    serializer = RootDomain.serializer(),
                    value = t
                ).encodeToByteArray(),
                output
            )
        }
    }


}