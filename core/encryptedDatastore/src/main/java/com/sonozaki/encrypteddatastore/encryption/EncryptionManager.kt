package com.sonozaki.encrypteddatastore.encryption

import java.io.InputStream
import java.io.OutputStream

/**
 * Class for files encryption.
 */
interface EncryptionManager {
    /**
     * Function for file encryption
     */
    fun encrypt(alias: String, bytes: ByteArray, outputStream: OutputStream)

    /**
     * Function for file decryption
     */
    fun decrypt(alias: String, inputStream: InputStream): ByteArray
}