package com.sonozaki.data.settings.repositories

import android.content.Context
import androidx.datastore.deviceProtectedDataStore
import com.sonozaki.data.settings.dataMigration.SettingsMigrationV1
import com.sonozaki.encrypteddatastore.encryption.EncryptedSerializer
import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for changing app settings
 */
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsSerializer: EncryptedSerializer<Settings>,
    settingsMigrationV1: SettingsMigrationV1
) : SettingsRepository {

    private val Context.settingsDatastore by deviceProtectedDataStore(
        DATASTORE_NAME,
        produceMigrations = {
            listOf(settingsMigrationV1)
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

    override suspend fun setRunOnDuressPassword(status: Boolean) {
        context.settingsDatastore.updateData {
            it.copy(runOnDuressPassword = status)
        }
    }

}
