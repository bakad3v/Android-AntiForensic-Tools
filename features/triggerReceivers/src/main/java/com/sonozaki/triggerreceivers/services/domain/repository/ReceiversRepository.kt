package com.sonozaki.triggerreceivers.services.domain.repository

import com.android.aftools.domain.entities.ButtonClicksData
import com.android.aftools.domain.entities.ButtonSettings
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface ReceiversRepository {
    suspend fun getPasswordStatus(): Boolean
    suspend fun getUsbSettings(): UsbSettings
    suspend fun getSettings(): Settings
    suspend fun getButtonSettings(): ButtonSettings
    suspend fun getButtonClicksData(): ButtonClicksData
    suspend fun onRightPassword()
    suspend fun onWrongPassword(): Boolean
    suspend fun setAdminActive(status: Boolean)
    suspend fun checkPassword(password: CharArray): Boolean
    suspend fun setServiceStatus(status: Boolean)
    suspend fun setRunOnBoot(status: Boolean)
    suspend fun setClicksInRow(clicks: Int)
    suspend fun setLastTimestamp(timestamp: Long)
}