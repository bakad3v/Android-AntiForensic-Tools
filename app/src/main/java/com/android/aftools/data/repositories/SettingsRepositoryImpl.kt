package com.android.aftools.data.repositories

import android.content.Context
import com.android.aftools.data.dataMigrations.SettingsMigrationV1
import com.android.aftools.data.encryption.EncryptedSerializer
import com.android.aftools.datastoreDBA.dataStoreDirectBootAware
import com.android.aftools.domain.entities.Settings
import com.android.aftools.domain.entities.Theme
import com.android.aftools.domain.repositories.SettingsRepository
import com.android.aftools.superuser.superuser.SuperUserManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for changing app settings
 */
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsSerializer: EncryptedSerializer<Settings>,
    settingsMigrationV1: SettingsMigrationV1,
    private val superUserManager: SuperUserManager
) : SettingsRepository {

    private val Context.settingsDatastore by dataStoreDirectBootAware(
        DATASTORE_NAME,
        produceMigrations = {
            context -> listOf<SettingsMigrationV1>(settingsMigrationV1)
        },
        serializer = settingsSerializer
    )

    companion object {
        private const val DATASTORE_NAME = "settings_datastore_v2.json"
    }

    override val settings: Flow<Settings> = context.settingsDatastore.data

    override suspend fun setTheme(theme: Theme) {
        context.settingsDatastore.updateData {
            it.copy(uiSettings = it.uiSettings.copy(theme = theme))
        }
    }

    override suspend fun setScreenshotsStatus(status: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(uiSettings = it.uiSettings.copy(allowScreenshots = status))
        }
    }

    override suspend fun setServiceStatus(working: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(serviceWorking = working)
        }
    }

    override suspend fun setRunOnBoot(status: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(runOnBoot = status)
        }
    }

    override suspend fun setDeleteApps(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(deleteApps = new)
        }
    }

    override suspend fun setDeleteFiles(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(deleteFiles = new)
        }
    }

    override suspend fun setDeleteProfiles(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(deleteProfiles = new)
        }
    }

    override suspend fun setTRIM(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(trim = new)
        }
    }

    override suspend fun setWipe(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(wipe = new)
        }
    }

    override suspend fun setRunRoot(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(runRoot = new)
        }
    }

    override suspend fun sendBroadcast(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(sendBroadcast = new)
        }
    }

    override suspend fun setRemoveItself(
        new: Boolean
    ) {
        context.settingsDatastore.updateData {
            it.copy(removeItself = new)
        }
    }

    override suspend fun setLogdOnStart(
        new: Boolean
    ) {
        context.settingsDatastore.updateData {
            it.copy(stopLogdOnStart = new)
        }
    }

    override suspend fun setLogdOnBoot(
        new: Boolean
    ) {
        context.settingsDatastore.updateData {
            it.copy(stopLogdOnBoot = new)
        }
    }

    override suspend fun setHide(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(hideItself = new)
        }
    }

    override suspend fun setClearData(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(clearData = new)
        }
    }

    override suspend fun setClearItself(new: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(clearItself = new)
        }
    }

    override suspend fun setUserLimit(limit: Int) {
        superUserManager.getSuperUser().setUsersLimit(limit)
    }


    override suspend fun setMultiuserUIStatus(status: Boolean) {
        superUserManager.getSuperUser().setMultiuserUI(status)
    }

    override suspend fun getMultiuserUIStatus(): Boolean =
        superUserManager.getSuperUser().getMultiuserUIStatus()

    override suspend fun setUserSwitcherStatus (status: Boolean) {
        superUserManager.getSuperUser().setUserSwitcherStatus(status)
    }

    override suspend fun getUserSwitcherStatus(): Boolean =
        superUserManager.getSuperUser().getUserSwitcherStatus()

    override suspend fun getSwitchUserRestriction(): Boolean =
        superUserManager.getSuperUser().getSwitchUserRestriction()

    override suspend fun setSwitchUserRestriction(status: Boolean) {
        superUserManager.getSuperUser().setSwitchUserRestriction(status)
    }

    override suspend fun getUserLimit(): Int? =
        superUserManager.getSuperUser().getUserLimit()

    override suspend fun setSafeBootStatus(status: Boolean) {
        superUserManager.getSuperUser().setSafeBootStatus(status)
    }

    override suspend fun getSafeBootStatus(): Boolean =
        superUserManager.getSuperUser().getSafeBootStatus()

    override suspend fun setRunOnDuressPassword(status: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(runOnDuressPassword = status)
        }
    }

}
