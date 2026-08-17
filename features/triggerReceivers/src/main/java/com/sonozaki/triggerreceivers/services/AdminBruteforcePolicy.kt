package com.sonozaki.triggerreceivers.services

import com.sonozaki.entities.BruteforceDetectingMethod
import com.sonozaki.entities.BruteforceSettings

internal fun shouldTriggerAdminBruteforce(
    settings: BruteforceSettings,
    failedAttempts: Int
): Boolean {
    return settings.detectingMethod == BruteforceDetectingMethod.ADMIN &&
        failedAttempts >= settings.allowedAttempts
}
