package com.sonozaki.password.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    private val passwordHasher = PasswordHasher(FakeArgon2Backend())

    @Test
    fun hash_usesUniqueSaltAndVerifiesPassword() {
        val password = "correct horse battery staple".toCharArray()
        val firstHash = passwordHasher.hash(password)
        val secondHash = passwordHasher.hash(password)

        assertNotEquals(firstHash.salt, secondHash.salt)
        assertNotEquals(firstHash.hash, secondHash.hash)
        assertEquals("argon2id", firstHash.algorithm)
        assertEquals(19, firstHash.algorithmVersion)
        assertEquals(32 * 1024, firstHash.memoryCostKiB)
        assertEquals(3, firstHash.iterations)
        assertEquals(1, firstHash.parallelism)
        assertTrue(passwordHasher.verify(password, firstHash))
        assertFalse(passwordHasher.verify("wrong password".toCharArray(), firstHash))
        assertFalse(passwordHasher.verify(password, firstHash.copy(algorithm = "pbkdf2")))
    }
}
