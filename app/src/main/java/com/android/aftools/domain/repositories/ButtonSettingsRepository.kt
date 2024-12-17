package com.android.aftools.domain.repositories

import com.android.aftools.domain.entities.ButtonClicksData
import com.android.aftools.domain.entities.ButtonSettings
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