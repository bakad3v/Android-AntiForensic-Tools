package com.oasisfeng.island.domain.repositories

import com.oasisfeng.island.domain.entities.ButtonClicksData
import com.oasisfeng.island.domain.entities.ButtonSettings
import kotlinx.coroutines.flow.Flow

interface ButtonSettingsRepository {
    val buttonSettings: Flow<ButtonSettings>
    suspend fun updateLatency(latency: Int)
    suspend fun updateAllowedClicks(allowedClicks: Int)
    fun getButtonClicksData(): ButtonClicksData
    fun setClicksInRow(clicks: Int)
    fun setLastTimestamp(timestamp: Long)
    suspend fun setTriggerOnButtonStatus(status: Boolean)
}