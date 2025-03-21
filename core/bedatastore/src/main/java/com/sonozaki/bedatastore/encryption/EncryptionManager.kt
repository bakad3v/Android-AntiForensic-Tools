package com.sonozaki.bedatastore.encryption

import java.io.InputStream
import java.io.OutputStream

/**
 * Class for files encryption.
 */
interface EncryptionManager {
    /**
     * Function for file encryption
     */
    fun encrypt(alias: String, bytes: ByteArray): ByteArray

    /**
     * Function for file decryption
     */
    fun decrypt(alias: String, data: ByteArray): ByteArray
}