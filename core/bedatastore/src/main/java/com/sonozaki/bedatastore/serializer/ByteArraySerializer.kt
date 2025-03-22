package com.sonozaki.bedatastore.serializer

import androidx.datastore.core.Serializer
import com.sonozaki.bedatastore.encryption.EncryptedSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

internal class ByteArraySerializer<T>(
    private val default: T,
    private val encryptedSerializer: EncryptedSerializer<T>, ): Serializer<ByteArray> {
    override val defaultValue: ByteArray
        get() = runBlocking {
            encryptedSerializer.serializeAndEncrypt(default)
        }

    override suspend fun readFrom(input: InputStream): ByteArray =  try {
            input.readBytes()
        } catch (e: Exception) {
            defaultValue
        }

    override suspend fun writeTo(t: ByteArray, output: OutputStream) {
            output.write(t)
    }
}