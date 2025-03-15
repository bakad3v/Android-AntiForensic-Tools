package com.android.aftools.presentation.dialogs

import com.android.aftools.presentation.utils.UIText

sealed class DialogActions {
  class ShowQuestionDialog(
    val title: UIText.StringResource,
    val message: UIText.StringResource,
    val requestKey: String,
    val hideCancel: Boolean = false,
    val cancellable: Boolean = true
  ) : DialogActions()

  class ShowInfoDialog(val title: UIText.StringResource, val message: UIText.StringResource) :
    DialogActions()

  class ShowInputDigitDialog(
    val title: UIText.StringResource,
    val hint: String,
    val message: UIText.StringResource,
    val range: IntRange,
    val requestKey: String
  ) :
    DialogActions()
}
