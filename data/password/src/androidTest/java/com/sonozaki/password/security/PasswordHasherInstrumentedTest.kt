package com.sonozaki.password.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PasswordHasherInstrumentedTest {
    @Test
    fun hash_usesSignalArgon2Backend() {
        val passwordHasher = PasswordHasher()
        val storedHash = passwordHasher.hash("correct horse battery staple".toCharArray())

        assertTrue(
            passwordHasher.verify("correct horse battery staple".toCharArray(), storedHash)
        )
        assertFalse(passwordHasher.verify("wrong password".toCharArray(), storedHash))
    }
}
