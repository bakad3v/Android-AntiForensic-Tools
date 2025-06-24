package com.sonozaki.data.settings.repositories

import com.sonozaki.entities.Settings
import com.sonozaki.entities.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  val settings: Flow<Settings>
  suspend fun setTheme(theme: Theme)
  suspend fun setServiceStatus(working: Boolean)
  suspend fun setRunOnBoot(status: Boolean)
  suspend fun setDeleteApps(new: Boolean)
  suspend fun setDeleteFiles(new: Boolean)
  suspend fun setDeleteProfiles(new: Boolean)
  suspend fun setTRIM(new: Boolean)
  suspend fun setWipe(new: Boolean)
  suspend fun setRunRoot(new: Boolean)
  suspend fun sendBroadcast(new: Boolean)
  suspend fun setRemoveItself(new: Boolean)
  suspend fun setLogdOnStart(new: Boolean)
  suspend fun setLogdOnBoot(new: Boolean)
  suspend fun setHide(new: Boolean)
  suspend fun setClearData(new: Boolean)
  suspend fun setClearItself(new: Boolean)
  suspend fun setRunOnDuressPassword(status: Boolean)
  suspend fun setScreenshotsStatus(status: Boolean)
}
