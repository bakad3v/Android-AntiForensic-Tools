package com.sonozaki.password.dataMigration

import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.password.entities.PasswordStatusV1
import com.sonozaki.password.security.FakeArgon2Backend
import com.sonozaki.password.security.PasswordHasher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHashMigrationMapperTest {
    private val testDispatcher = StandardTestDispatcher()
    private val passwordHasher = PasswordHasher(FakeArgon2Backend())
    private val mapper = PasswordHashMigrationMapper(passwordHasher, testDispatcher)

    @Test
    fun invoke_replacesLegacyPasswordWithHash() = runTest(testDispatcher.scheduler) {
        val oldStatus = Json.decodeFromString(
            PasswordStatusV1.serializer(),
            """{"password":"legacy password","passwordSet":true}"""
        )

        val migratedStatus = mapper(oldStatus)
        val migratedHash = migratedStatus.passwordHash

        assertNotNull(migratedHash)
        assertTrue(migratedStatus.passwordSet)
        assertTrue(passwordHasher.verify("legacy password".toCharArray(), migratedHash!!))

        val serializedStatus = Json.encodeToString(
            PasswordStatus.serializer(),
            migratedStatus
        )
        assertTrue(serializedStatus.contains("\"passwordHash\""))
        assertFalse(serializedStatus.contains("\"password\":"))
    }
}
