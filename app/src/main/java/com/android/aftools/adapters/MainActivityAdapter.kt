package com.android.aftools.adapters

import android.content.Context
import com.android.aftools.domain.repository.MainActivityRepository
import com.bakasoft.appupdater.repository.AppUpdateRepository
import com.bakasoft.network.RequestResult
import com.sonozaki.data.settings.repositories.PermissionsRepository
import com.sonozaki.data.settings.repositories.SettingsRepository
import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UISettings
import com.sonozaki.superuser.superuser.SuperUserManager
import com.sonozaki.utils.TopLevelFunctions.isTestOnlyApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainActivityAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionsRepository: PermissionsRepository,
    private val appUpdateRepository: AppUpdateRepository,
    private val superUserManager: SuperUserManager,
    @ApplicationContext private val context: Context
): MainActivityRepository {
    override val uiSettings: Flow<UISettings>
        get() = settingsRepository.settings.map { it.uiSettings }
    override val appLatestData: Flow<RequestResult<AppLatestVersion>>
        get() = appUpdateRepository.appUpdateDataFlow
    override val displayUpdateNotification: Flow<Boolean>
        get() = appUpdateRepository.showUpdatePopupStatus

    override suspend fun disableAdmin() {
        try {
            superUserManager.removeAdminRights()
        } catch (e: Exception) {}
    }

    override suspend fun savedTestOnlyStatus(): Boolean {
        return appUpdateRepository.isTestOnlyStatus.first()
    }

    override fun isTestOnly(): Boolean {
        return context.isTestOnlyApp()
    }

    override suspend fun saveTestOnlyStatus(status: Boolean) {
        appUpdateRepository.saveTestOnlyStatus(status)
    }

    override val testOnlyNeeded: Flow<Boolean> = combineTransform(
        settingsRepository.settings, permissionsRepository.permissions) {
            settings: Settings, permissions: Permissions ->  permissions.isAdmin && permissions.isRoot && settings.removeItself
    }

    override suspend fun checkUpdates() {
        appUpdateRepository.checkUpdates()
    }
}