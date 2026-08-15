package com.sonozaki.password.dataMigration

import com.sonozaki.password.entities.PasswordStatus
import com.sonozaki.password.entities.PasswordStatusV1
import com.sonozaki.password.security.PasswordHasher
import com.sonozaki.resources.DEFAULT_DISPATCHER
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class PasswordHashMigrationMapper @Inject constructor(
    private val passwordHasher: PasswordHasher,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(oldStatus: PasswordStatusV1): PasswordStatus {
        if (!oldStatus.passwordSet) {
            return PasswordStatus()
        }

        val password = oldStatus.password.toCharArray()
        return try {
            val passwordHash = withContext(defaultDispatcher) {
                passwordHasher.hash(password)
            }
            PasswordStatus(passwordHash = passwordHash, passwordSet = true)
        } finally {
            password.fill('\u0000')
        }
    }
}
