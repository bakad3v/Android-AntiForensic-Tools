package com.bakasoft.setupwizard.domain.repository

import com.sonozaki.entities.AppLatestVersion
import com.sonozaki.entities.BruteforceSettings
import com.sonozaki.entities.ButtonSettings
import com.sonozaki.entities.DeviceProtectionSettings
import com.sonozaki.entities.Permissions
import com.sonozaki.entities.Settings
import com.sonozaki.entities.UsbSettings
import kotlinx.coroutines.flow.Flow

interface SetupWizardRepository {
    val permissions: Flow<Permissions>
    val settings: Flow<Settings>
    val filesSelected: Flow<Boolean>
    val profilesSelected: Flow<Boolean>
    val usbSettings: Flow<UsbSettings>
    val buttonSettings: Flow<ButtonSettings>
    val bruteforceSettings: Flow<BruteforceSettings>
    val deviceProtectionSettings: Flow<DeviceProtectionSettings>
    val appLatestData: Flow<AppLatestVersion?>
    val rootCommandNotEmpty: Flow<Boolean>

    val listeningNotifications: Flow<Boolean>

    suspend fun checkUpdates()
    suspend fun refreshProfiles()

    suspend fun setTriggerOnUsb()
    suspend fun setTriggerOnDuressPassword()
    suspend fun setTriggerOnVolumeButtonClicks()
    suspend fun setTriggerOnPowerButtonClicks()
    suspend fun setTriggerOnBruteforce()

    suspend fun setSelfDestruction()
    suspend fun setAppHiding()
    suspend fun disableLogs()
    suspend fun disableSafeBoot()
    suspend fun setupMultiuser()
    suspend fun activateTrim()
}