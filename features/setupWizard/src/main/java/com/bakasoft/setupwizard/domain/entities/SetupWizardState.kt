package com.bakasoft.setupwizard.domain.entities

import java.util.EnumMap

sealed class SetupWizardState {
    data object Loading: SetupWizardState()
    data class Data(val dataMap: EnumMap<WizardElement, SettingsElementState>, val state: AppState,
                    val dataSelected: DataSelected, val protectionFixActive: Boolean, val triggersFixActive: Boolean,
        val permissionsState: PermissionsState): SetupWizardState()
}

enum class DataSelected {
    PROFILES, FILES, WIPE, ROOT, NONE
}

enum class AppState {
    NICE, WITH_WARNINGS, BAD
}

enum class WizardElement {
    CORRECT_APP_VERSION, ACCESSIBILITY_SERVICE, SUPERUSER_PERMISSIONS, TRIGGER_ON_USB,
    TRIGGER_ON_PASSWORD, TRIGGER_ON_CLICKS, TRIGGER_ON_BRUTEFORCE, SELECT_DATA, ACTIVATE_DESTRUCTION,
    ENABLE_SELF_DESTRUCTION, ENABLE_HIDING, DISABLE_LOGS, DISABLE_SAFE_BOOT, SETUP_MULTIUSER, ACTIVATE_TRIM
}