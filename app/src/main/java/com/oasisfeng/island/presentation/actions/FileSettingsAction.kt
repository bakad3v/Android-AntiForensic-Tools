package com.oasisfeng.island.presentation.actions

import com.oasisfeng.island.presentation.utils.UIText

sealed class FileSettingsAction {
  class ShowUsualDialog(val value: DialogActions): FileSettingsAction()
  class ShowPriorityEditor(val title: UIText.StringResource, val hint: String, val message: UIText.StringResource, val uri: String, val range: IntRange): FileSettingsAction()
}
