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
    suspend fun disableAdmin()
    suspend fun savedTestOnlyStatus(): Boolean
    suspend fun saveTestOnlyStatus(status: Boolean)
    fun isTestOnly(): Boolean
}