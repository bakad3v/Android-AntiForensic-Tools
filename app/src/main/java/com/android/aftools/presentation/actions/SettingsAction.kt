package com.android.aftools.presentation.actions

import com.android.aftools.presentation.dialogs.DialogActions

sealed class SettingsAction {
    class ShowDialog(val value: DialogActions): SettingsAction()
    data object ShowFaq: SettingsAction()
}