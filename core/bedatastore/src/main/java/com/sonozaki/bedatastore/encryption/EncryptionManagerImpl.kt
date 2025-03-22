package com.sonozaki.bedatastore.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class EncryptionManagerImpl(): EncryptionManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getEncryptCipher(alias: String) = Cipher.getInstance(TRANSFORMATION).apply {
        Log.w("input","started")
        init(Cipher.ENCRYPT_MODE, getKey(alias))
        Log.w("input","finished")
    }

    private fun getDecryptCipherForIv(alias: String,iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(alias), IvParameterSpec(iv))
        }
    }

    private fun getKey(alias: String): SecretKey {
        val existingKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(alias)
    }

    private fun createKey(alias: String): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }


    override fun encrypt(alias: String, bytes: ByteArray): ByteArray {
        val encryptCypher = getEncryptCipher(alias)
        val encryptedBytes = encryptCypher.doFinal(bytes)
        val outputStream = ByteArrayOutputStream()
        outputStream.use {
            it.write(encryptCypher.iv.size)
            it.write(encryptCypher.iv)
            it.write(encryptedBytes)
        }
        return outputStream.toByteArray()
    }


    override fun decrypt(alias: String, data: ByteArray): ByteArray {
        return data.inputStream().use {
            val ivSize = it.read()
            Log.w("logEventivSize",ivSize.toString())
            val iv = ByteArray(ivSize)
            it.read(iv)
            val encryptedBytes = it.readBytes()
            getDecryptCipherForIv(alias,iv).doFinal(encryptedBytes)
        }
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}