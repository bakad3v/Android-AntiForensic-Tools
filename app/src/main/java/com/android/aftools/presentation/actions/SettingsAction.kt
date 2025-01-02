package com.android.aftools.presentation.actions

sealed class SettingsAction {
    class ShowDialog(val value: DialogActions): SettingsAction()
    object ShowFaq: SettingsAction()
}