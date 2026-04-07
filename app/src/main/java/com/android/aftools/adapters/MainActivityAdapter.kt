package com.android.aftools.adapters

import com.android.aftools.domain.repository.MainActivityRepository
import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainActivityAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionsRepository: PermissionsRepository,
    private val appUpdateRepository: AppUpdateRepository,
): MainActivityRepository {
    override val uiSettings: Flow<UISettings>
        get() = settingsRepository.settings.map { it.uiSettings }
    override val appLatestData: Flow<RequestResult<AppLatestVersion>>
        get() = appUpdateRepository.appUpdateDataFlow
    override val displayUpdateNotification: Flow<Boolean>
        get() = appUpdateRepository.showUpdatePopupStatus

    override val testOnlyNeeded: Flow<Boolean> = combineTransform(
        settingsRepository.settings, permissionsRepository.permissions) {
            settings: Settings, permissions: Permissions ->  permissions.isAdmin && permissions.isRoot && settings.removeItself
    }

    override suspend fun checkUpdates() {
        appUpdateRepository.checkUpdates()
    }
}