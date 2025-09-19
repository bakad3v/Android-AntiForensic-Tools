package com.android.aftools.domain.repository

import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow

interface MainActivityRepository {
    val uiSettings: Flow<UISettings>
    val appLatestData: Flow<RequestResult<AppLatestVersion>>
    val displayUpdateNotification: Flow<Boolean>
    suspend fun checkUpdates()
}