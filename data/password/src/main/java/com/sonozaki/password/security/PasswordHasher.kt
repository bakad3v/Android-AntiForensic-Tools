package com.sonozaki.password.security

import com.sonozaki.password.entities.PasswordHash
import org.signal.argon2.Argon2
import org.signal.argon2.MemoryCost
import org.signal.argon2.Type
import org.signal.argon2.Version
import java.nio.CharBuffer
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordHasher internal constructor(
    private val argon2Backend: Argon2Backend
) {
    @Inject
    constructor() : this(SignalArgon2Backend())

    private val secureRandom = SecureRandom()

    fun hash(password: CharArray): PasswordHash {
        val passwordBytes = password.toUtf8Bytes()
        val salt = ByteArray(SALT_LENGTH_BYTES).also(secureRandom::nextBytes)
        var derivedKey: ByteArray? = null

        return try {
            derivedKey = argon2Backend.hash(passwordBytes, salt, CURRENT_PARAMETERS)
            check(derivedKey.size == HASH_LENGTH_BYTES) {
                "Unexpected Argon2 hash length: ${derivedKey.size}"
            }
            PasswordHash(
                hash = Base64.getEncoder().encodeToString(derivedKey),
                salt = Base64.getEncoder().encodeToString(salt),
                algorithm = ALGORITHM,
                algorithmVersion = ALGORITHM_VERSION,
                memoryCostKiB = CURRENT_PARAMETERS.memoryCostKiB,
                iterations = CURRENT_PARAMETERS.iterations,
                parallelism = CURRENT_PARAMETERS.parallelism
            )
        } finally {
            passwordBytes.fill(0)
            salt.fill(0)
            derivedKey?.fill(0)
        }
    }

    fun verify(password: CharArray, storedHash: PasswordHash): Boolean {
        if (!storedHash.hasSupportedParameters()) {
            return false
        }

        val salt = decode(storedHash.salt) ?: return false
        val expectedHash = decode(storedHash.hash) ?: run {
            salt.fill(0)
            return false
        }

        if (salt.size != SALT_LENGTH_BYTES || expectedHash.size != HASH_LENGTH_BYTES) {
            salt.fill(0)
            expectedHash.fill(0)
            return false
        }

        val passwordBytes = password.toUtf8Bytes()
        var actualHash: ByteArray? = null
        return try {
            actualHash = argon2Backend.hash(
                passwordBytes,
                salt,
                Argon2Parameters(
                    memoryCostKiB = storedHash.memoryCostKiB,
                    iterations = storedHash.iterations,
                    parallelism = storedHash.parallelism,
                    hashLength = HASH_LENGTH_BYTES
                )
            )
            MessageDigest.isEqual(expectedHash, actualHash)
        } finally {
            passwordBytes.fill(0)
            salt.fill(0)
            expectedHash.fill(0)
            actualHash?.fill(0)
        }
    }

    private fun PasswordHash.hasSupportedParameters(): Boolean {
        return algorithm == ALGORITHM &&
            algorithmVersion == ALGORITHM_VERSION &&
            memoryCostKiB in MIN_MEMORY_COST_KIB..MAX_MEMORY_COST_KIB &&
            iterations in MIN_ITERATIONS..MAX_ITERATIONS &&
            parallelism in MIN_PARALLELISM..MAX_PARALLELISM &&
            memoryCostKiB >= 8 * parallelism
    }

    private fun CharArray.toUtf8Bytes(): ByteArray {
        val byteBuffer = Charsets.UTF_8.encode(CharBuffer.wrap(this))
        val bytes = ByteArray(byteBuffer.remaining())
        byteBuffer.get(bytes)
        if (byteBuffer.hasArray()) {
            byteBuffer.array().fill(0)
        }
        return bytes
    }

    private fun decode(value: String): ByteArray? {
        return try {
            Base64.getDecoder().decode(value)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    companion object {
        private const val ALGORITHM = "argon2id"
        private const val ALGORITHM_VERSION = 19
        private const val SALT_LENGTH_BYTES = 16
        private const val HASH_LENGTH_BYTES = 32

        private const val MIN_MEMORY_COST_KIB = 8 * 1024
        private const val MAX_MEMORY_COST_KIB = 128 * 1024
        private const val MIN_ITERATIONS = 1
        private const val MAX_ITERATIONS = 10
        private const val MIN_PARALLELISM = 1
        private const val MAX_PARALLELISM = 4

        private val CURRENT_PARAMETERS = Argon2Parameters(
            memoryCostKiB = 32 * 1024,
            iterations = 6,
            parallelism = 1,
            hashLength = HASH_LENGTH_BYTES
        )
    }
}

internal data class Argon2Parameters(
    val memoryCostKiB: Int,
    val iterations: Int,
    val parallelism: Int,
    val hashLength: Int
)

internal fun interface Argon2Backend {
    fun hash(password: ByteArray, salt: ByteArray, parameters: Argon2Parameters): ByteArray
}

private class SignalArgon2Backend : Argon2Backend {
    override fun hash(
        password: ByteArray,
        salt: ByteArray,
        parameters: Argon2Parameters
    ): ByteArray {
        val argon2 = Argon2.Builder(Version.V13)
            .type(Type.Argon2id)
            .memoryCost(MemoryCost.KiB(parameters.memoryCostKiB))
            .parallelism(parameters.parallelism)
            .iterations(parameters.iterations)
            .hashLength(parameters.hashLength)
            .build()
        return argon2.hash(password, salt).hash
    }
}
