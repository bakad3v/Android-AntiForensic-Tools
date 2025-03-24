package com.sonozaki.logs.presentation.actions

sealed class LogsActions {
  class ShowUsualDialog(val value: com.sonozaki.dialogs.DialogActions): LogsActions()
  class ShowDatePicker(val dateValidator: com.sonozaki.utils.DateValidatorAllowed, val selection: Long): LogsActions()

}
