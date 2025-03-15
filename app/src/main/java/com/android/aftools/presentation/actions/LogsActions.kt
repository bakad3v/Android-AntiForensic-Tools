package com.android.aftools.presentation.actions

import com.android.aftools.presentation.dialogs.DialogActions
import com.android.aftools.presentation.utils.DateValidatorAllowed

sealed class LogsActions {
  class ShowUsualDialog(val value: DialogActions): LogsActions()
  class ShowDatePicker(val dateValidator: DateValidatorAllowed, val selection: Long): LogsActions()

}
