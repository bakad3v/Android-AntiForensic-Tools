package net.typeblog.shelter.presentation.actions

import net.typeblog.shelter.presentation.utils.DateValidatorAllowed

sealed class LogsActions {
  class ShowUsualDialog(val value: DialogActions): LogsActions()
  class ShowDatePicker(val dateValidator: DateValidatorAllowed, val selection: Long): LogsActions()

}
