package com.sonozaki.data.settings.repositories

import com.sonozaki.entities.ButtonClicksData
import com.sonozaki.entities.ButtonSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository for storing power button trigger settings
 */
interface ButtonSettingsRepository {
    /**
     * Power button trigger settings
     */
    val buttonSettings: Flow<ButtonSettings>

    /**
     * Setup maximum delay between clicks in row. If delay between clicks is bigger than latency, they are counted as separate clicks and count starts again.
     */
    suspend fun updateLatency(latency: Int)

    /**
     * Setup number of clicks to activate data destruction.
     */
    suspend fun updateAllowedClicks(allowedClicks: Int)

    /**
     * Get number of power button clicks and timestamp for last click
     */
    fun getButtonClicksData(): ButtonClicksData

    /**
     * Set number of performed power button clicks in row
     */
    fun setClicksInRow(clicks: Int)

    /**
     * Set timestamp for last click
     */
    fun setLastTimestamp(timestamp: Long)

    /**
     * Enable or disable destroying data on power button click
     */
    suspend fun setTriggerOnButtonStatus(status: Boolean)
}