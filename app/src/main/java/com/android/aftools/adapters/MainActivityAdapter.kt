package com.android.aftools.adapters

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import com.android.aftools.domain.repository.DeviceAdminActivator
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainActivityAdapter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionsRepository: PermissionsRepository,
    private val appUpdateRepository: AppUpdateRepository,
    private val superUserManager: SuperUserManager,
    private val userManager: UserManager,
    @ApplicationContext private val context: Context
): MainActivityRepository {
    override val uiSettings: Flow<UISettings>
        get() = settingsRepository.settings.map { it.uiSettings }
    override val appLatestData: Flow<RequestResult<AppLatestVersion>>
        get() = appUpdateRepository.appUpdateDataFlow
    override val displayUpdateNotification: Flow<Boolean>
        get() = appUpdateRepository.showUpdatePopupStatus

    override fun isUserUnlocked(): Boolean = userManager.isUserUnlocked

    override fun hasAdminRights(): Boolean = superUserManager.hasAdminRights()

    override suspend fun isAdminReactivationPending(): Boolean =
        appUpdateRepository.isAdminReactivationPending.first()

    override suspend fun saveAdminReactivationPending(pending: Boolean) =
        appUpdateRepository.saveAdminReactivationPending(pending)

    override suspend fun removeAdminRights() = superUserManager.removeAdminRights()

    override suspend fun getDeviceAdminActivator(): DeviceAdminActivator? {
        val backend = superUserManager.getDeviceAdminActivator() ?: return null
        return DeviceAdminActivator { backend.activateDeviceAdmin() }
    }

    override suspend fun savedTestOnlyStatus(): Boolean? {
        return appUpdateRepository.isTestOnlyStatus.first()
    }

    override fun isTestOnly(): Boolean {
        return context.isTestOnlyApp()
    }

    override fun isAppUpdated(): Boolean {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.lastUpdateTime > packageInfo.firstInstallTime
        } catch (_: PackageManager.NameNotFoundException) {
            // The current package must exist. Treat an unexpected lookup failure as an update so
            // an unknown TEST_ONLY transition cannot silently retain device-admin rights.
            true
        }
    }

    override suspend fun saveTestOnlyStatus(status: Boolean) {
        appUpdateRepository.saveTestOnlyStatus(status)
    }

    override val testOnlyNeeded: Flow<Boolean> = combine(
        settingsRepository.settings, permissionsRepository.permissions) {
            settings: Settings, permissions: Permissions ->
            permissions.isAdmin && permissions.isRoot && settings.removeItself
    }

    override suspend fun checkUpdates() {
        appUpdateRepository.checkUpdates()
    }
}
