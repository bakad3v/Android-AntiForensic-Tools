package com.android.aftools.adapters

import com.bakasoft.appinstaller.domain.repository.AppInstallerServiceRepository
import com.bakasoft.appupdatecenter.domain.repository.AppUpdateCenterRepository
import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.entities.AppInstallerData
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.Permissions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppUpdaterAdapter @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository,
    private val permissionsRepository: PermissionsRepository,
    private val appInstallerServiceRepository: AppInstallerServiceRepository
): AppUpdateCenterRepository {
    override val showUpdatePopupStatus: Flow<Boolean>
        get() = appUpdateRepository.showUpdatePopupStatus
    override val appUpdateDataFlow: Flow<RequestResult<AppLatestVersion>>
        get() = appUpdateRepository.appUpdateDataFlow
    override val permissions: Flow<Permissions>
        get() = permissionsRepository.permissions

    override suspend fun setUpdatePopupStatus(status: Boolean) {
        appUpdateRepository.setUpdatePopupStatus(status)
    }

    override suspend fun setInstallerData(data: AppInstallerData) {
        appInstallerServiceRepository.setInstallerData(data)
    }

    override suspend fun checkUpdates() {
        appUpdateRepository.checkUpdates()
    }
}