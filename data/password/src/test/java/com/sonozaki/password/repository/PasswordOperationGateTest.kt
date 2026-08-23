package com.sonozaki.password.repository

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

class PasswordOperationGateTest {
    @Test
    fun runExclusive_serializesPasswordOperations() = runTest {
        val gate = PasswordOperationGate()
        var activeOperations = 0
        var maximumActiveOperations = 0

        coroutineScope {
            repeat(5) {
                launch {
                    gate.runExclusive {
                        activeOperations++
                        maximumActiveOperations = maxOf(
                            maximumActiveOperations,
                            activeOperations
                        )
                        yield()
                        activeOperations--
                    }
                }
            }
        }

        assertEquals(1, maximumActiveOperations)
    }
}
