package com.android.aftools.data.serializers

import androidx.datastore.core.Serializer
import com.android.aftools.data.encryption.EncryptionAlias
import com.android.aftools.data.encryption.EncryptionManager
import com.android.aftools.domain.entities.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SettingsSerializer @Inject constructor(private val encryptionManager: EncryptionManager): Serializer<Settings> {

  override val defaultValue: Settings
    get() = Settings()


  override suspend fun readFrom(input: InputStream): Settings {
    val decryptedBytes = encryptionManager.decrypt(EncryptionAlias.DATASTORE.name,input)
    return try {
      Json.decodeFromString(deserializer = Settings.serializer(),
        string = decryptedBytes.decodeToString())
    } catch (e: SerializationException) {
      defaultValue
    }
  }

  override suspend fun writeTo(t: Settings, output: OutputStream) {
    withContext(Dispatchers.IO) {
      encryptionManager.encrypt(
        EncryptionAlias.DATASTORE.name,
        Json.encodeToString(
          serializer = Settings.serializer(),
          value = t
        ).encodeToByteArray(),
        output
      )
    }
  }


}
