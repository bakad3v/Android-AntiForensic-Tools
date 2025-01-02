package net.typeblog.shelter.presentation.actions

import net.typeblog.shelter.presentation.utils.UIText

sealed class FileSettingsAction {
  class ShowUsualDialog(val value: DialogActions): FileSettingsAction()
  class ShowPriorityEditor(val title: UIText.StringResource, val hint: String, val message: UIText.StringResource, val uri: String, val range: IntRange): FileSettingsAction()
}
