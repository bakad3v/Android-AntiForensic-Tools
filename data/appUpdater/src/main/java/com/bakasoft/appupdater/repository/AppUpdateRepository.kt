package com.bakasoft.appupdater.repository

import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppLatestVersion
import kotlinx.coroutines.flow.Flow

interface AppUpdateRepository {
    val showUpdatePopupStatus: Flow<Boolean>
    val appUpdateDataFlow: Flow<RequestResult<AppLatestVersion>>
    suspend fun setUpdatePopupStatus(status: Boolean)
    suspend fun checkUpdates()
}