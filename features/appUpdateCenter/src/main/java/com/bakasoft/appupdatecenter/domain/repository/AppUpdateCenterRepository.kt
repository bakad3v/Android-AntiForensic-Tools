package com.bakasoft.appupdatecenter.domain.repository

import com.bakasoft.network.RequestResult
import com.sonozaki.entities.AppInstallerData
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow

interface AppUpdateCenterRepository {
    val showUpdatePopupStatus: Flow<Boolean>
    val appUpdateDataFlow: Flow<RequestResult<AppLatestVersion>>
    val permissions: Flow<Permissions>
    suspend fun setUpdatePopupStatus(status: Boolean)
    suspend fun setInstallerData(data: AppInstallerData)
    suspend fun checkUpdates()
}