package com.sonozaki.settings.repositories

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
  suspend fun setUserLimit(limit: Int)
  suspend fun getUserLimit(): Int?
  suspend fun setRunOnDuressPassword(status: Boolean)
  suspend fun setMultiuserUIStatus(status: Boolean)
  suspend fun setSafeBootStatus(status: Boolean)
  suspend fun getSafeBootStatus(): Boolean
  suspend fun setSwitchUserRestriction(status: Boolean)
  suspend fun getSwitchUserRestriction(): Boolean
  suspend fun getUserSwitcherStatus(): Boolean
  suspend fun setUserSwitcherStatus(status: Boolean)
  suspend fun getMultiuserUIStatus(): Boolean
  suspend fun setScreenshotsStatus(status: Boolean)
}
