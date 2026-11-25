package com.android.aftools.domain.repository

import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow

interface MainActivityRepository {
    val uiSettings: Flow<UISettings>
    val appLatestData: Flow<RequestResult<AppLatestVersion>>
    val displayUpdateNotification: Flow<Boolean>
    val testOnlyNeeded: Flow<Boolean>
    suspend fun checkUpdates()
    fun isUserUnlocked(): Boolean
    fun hasAdminRights(): Boolean
    suspend fun isAdminReactivationPending(): Boolean
    suspend fun saveAdminReactivationPending(pending: Boolean)
    suspend fun removeAdminRights()
    suspend fun getDeviceAdminActivator(): DeviceAdminActivator?
    suspend fun savedTestOnlyStatus(): Boolean?
    suspend fun saveTestOnlyStatus(status: Boolean)
    fun isTestOnly(): Boolean
    fun isAppUpdated(): Boolean
}

/** A live, preflighted backend retained across admin removal and activation. */
fun interface DeviceAdminActivator {
    suspend fun activate()
}
