package com.sonozaki.settings.presentation.actions

import com.sonozaki.dialogs.DialogActions


sealed class SettingsAction {
    class ShowDialog(val value: DialogActions): SettingsAction()
    data object ShowFaq: SettingsAction()
}