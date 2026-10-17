package com.sonozaki.password.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/** Prevents memory-hard password operations from running concurrently. */
class PasswordOperationGate @Inject constructor() {
    private val mutex = Mutex()

    suspend fun <T> runExclusive(operation: suspend () -> T): T = mutex.withLock {
        operation()
    }
}
