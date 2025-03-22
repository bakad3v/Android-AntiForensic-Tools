package com.sonozaki.bedatastore.corruptionHandler

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.sonozaki.bedatastore.encryption.EncryptedSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun <T> encryptedCorruptionHandler(
    baseHandler: ReplaceFileCorruptionHandler<T>?,
    encryptedSerializer: EncryptedSerializer<T>
): ReplaceFileCorruptionHandler<ByteArray>? {
    return baseHandler?.let {
        ReplaceFileCorruptionHandler { ex: CorruptionException ->
            return@ReplaceFileCorruptionHandler runBlocking {
                encryptedSerializer.serializeAndEncrypt(baseHandler.handleCorruption(ex))
            }
        }
    }
}