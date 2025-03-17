package com.sonozaki.settings.mappers

import com.sonozaki.entities.Settings
import com.sonozaki.settings.entities.SettingsV1
import com.sonozaki.entities.UISettings
import javax.inject.Inject

class SettingsVersionMapper @Inject constructor() {
    fun mapOldSettingsToNew(old: SettingsV1): Settings = Settings(
        uiSettings = UISettings(allowScreenshots = false, theme = old.theme),
        deleteFiles = old.deleteFiles,
        deleteProfiles = old.deleteProfiles,
        deleteApps = old.deleteApps,
        clearData = old.clearData,
        serviceWorking = old.serviceWorking,
        trim = old.trim,
        wipe = old.wipe,
        removeItself = old.removeItself,
        clearItself = old.clearItself,
        sendBroadcast = old.sendBroadcast,
        runRoot = old.runRoot,
        runOnBoot = old.runOnBoot,
        stopLogdOnBoot = old.stopLogdOnBoot,
        stopLogdOnStart = old.stopLogdOnStart,
        runOnDuressPassword = old.runOnDuressPassword,
        hideItself = old.hideItself
    )
}