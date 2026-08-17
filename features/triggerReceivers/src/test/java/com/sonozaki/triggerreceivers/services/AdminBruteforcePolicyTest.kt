package com.sonozaki.triggerreceivers.services

import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminBruteforcePolicyTest {

    @Test
    fun shouldTriggerAdminBruteforce_triggersAtAndAfterLimit() {
        val settings = BruteforceSettings(
            allowedAttempts = 10,
            detectingMethod = BruteforceDetectingMethod.ADMIN
        )

        assertFalse(shouldTriggerAdminBruteforce(settings, failedAttempts = 9))
        assertTrue(shouldTriggerAdminBruteforce(settings, failedAttempts = 10))
        assertTrue(shouldTriggerAdminBruteforce(settings, failedAttempts = 11))
    }

    @Test
    fun shouldTriggerAdminBruteforce_ignoresOtherDetectionMethods() {
        BruteforceDetectingMethod.entries
            .filterNot { it == BruteforceDetectingMethod.ADMIN }
            .forEach { method ->
                val settings = BruteforceSettings(
                    allowedAttempts = 10,
                    detectingMethod = method
                )

                assertFalse(shouldTriggerAdminBruteforce(settings, failedAttempts = 10))
            }
    }
}
