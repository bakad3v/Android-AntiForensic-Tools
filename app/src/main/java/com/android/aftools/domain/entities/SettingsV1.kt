package com.android.aftools.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class SettingsV1(
    val deleteFiles: Boolean = false,
    val deleteApps: Boolean = false,
    val deleteProfiles: Boolean = false,
    val serviceWorking: Boolean = false,
    val runOnBoot: Boolean = false,
    val theme: Theme = Theme.SYSTEM_THEME,
    val trim: Boolean = false,
    val wipe: Boolean = false,
    val runRoot: Boolean = false,
    val sendBroadcast: Boolean = false,
    val removeItself: Boolean = false,
    val hideItself: Boolean = false,
    val clearItself: Boolean = false,
    val clearData: Boolean = false,
    val stopLogdOnBoot: Boolean = false,
    val stopLogdOnStart: Boolean = false,
    val runOnDuressPassword: Boolean = false
)