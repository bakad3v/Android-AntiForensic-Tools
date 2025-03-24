package com.sonozaki.encrypteddatastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class BaseSerializer<T> @Inject constructor(private val dispatcherIO: CoroutineDispatcher, private val serializer: KSerializer<T>, override val defaultValue: T): Serializer<T> {
    override suspend fun readFrom(input: InputStream): T = withContext(dispatcherIO) {
        return@withContext try {
            val inputBytes = input.readBytes().decodeToString()
            Json.decodeFromString(
                deserializer = serializer,
                string = inputBytes
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(dispatcherIO) {
            val resultString = Json.encodeToString(
                serializer = serializer,
                value = t
            )
            output.write(resultString.encodeToByteArray())
        }
    }
}