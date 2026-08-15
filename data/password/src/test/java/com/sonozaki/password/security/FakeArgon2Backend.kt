package com.sonozaki.password.security

import java.nio.ByteBuffer
import java.security.MessageDigest

internal class FakeArgon2Backend : Argon2Backend {
    override fun hash(
        password: ByteArray,
        salt: ByteArray,
        parameters: Argon2Parameters
    ): ByteArray {
        val parameterBytes = ByteBuffer.allocate(Int.SIZE_BYTES * 4)
            .putInt(parameters.memoryCostKiB)
            .putInt(parameters.iterations)
            .putInt(parameters.parallelism)
            .putInt(parameters.hashLength)
            .array()
        return MessageDigest.getInstance("SHA-256").run {
            update(parameterBytes)
            update(salt)
            digest(password)
        }
    }
}
