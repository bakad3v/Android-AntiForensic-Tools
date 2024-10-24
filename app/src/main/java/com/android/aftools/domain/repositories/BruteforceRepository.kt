package com.android.aftools.domain.repositories

import com.android.aftools.domain.entities.BruteforceSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository for storing settings of bruteforce prevention system. Data is encrypted.
 */
interface BruteforceRepository {
    val bruteforceSettings: Flow<BruteforceSettings>

    /**
     * Enforce or disable bruteforce prevention system
     */
    suspend fun setBruteforceStatus(status: Boolean)

    /**
     * Set number of wrong password attempts. On n-th wrong attempt app will react.
     */
    suspend fun setBruteforceLimit(limit: Int)

    /**
     * React on wrong password. If number of wrong password attempts passes the threshold app will react.
     */
    suspend fun onWrongPassword(): Boolean

    /**
     * React on rights password. Reset counter of wrong password attempts.
     */
    suspend fun onRightPassword()
}