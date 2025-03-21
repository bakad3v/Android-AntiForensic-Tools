package com.sonozaki.bedatastore.encryption

import android.util.Log
import androidx.datastore.core.Serializer
import java.io.ByteArrayOutputStream

internal class EncryptedSerializer<T>(
    private val encryptionManager: EncryptionManager,
    private val serializer: Serializer<T>,
    private val alias: String
) {
    suspend fun decryptAndDeserialize(encryptedData: ByteArray): T {
        val decryptedData = encryptionManager.decrypt(alias, encryptedData)
        val inputStream = decryptedData.inputStream()
        return serializer.readFrom(inputStream)
    }

    suspend fun serializeAndEncrypt(newData: T): ByteArray {
        Log.w("input",newData.toString())
        val outputStream = ByteArrayOutputStream()
        serializer.writeTo(newData, outputStream)
        val result = outputStream.toByteArray()
        Log.w("input",newData.toString())
        return encryptionManager.encrypt(alias, result)
    }
}