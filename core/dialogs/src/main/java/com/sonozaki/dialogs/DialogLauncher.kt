package com.sonozaki.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager

/**
 * Class for launching dialog depending on the type of dialogAction
 */
class DialogLauncher (
  private val fragmentManager: FragmentManager,
  val context: Context?) {
  fun launchDialogFromAction(action: DialogActions) {
    when(action){
      is DialogActions.ShowInfoDialog -> with(action) {
        showInfoDialog(
          title.asString(context), message.asString(context)
        )
      }

      is DialogActions.ShowQuestionDialog -> with(action) {
        showQuestionDialog(title.asString(context), message.asString(context), requestKey, hideCancel, cancellable)
      }
      is DialogActions.ShowInputDigitDialog -> with(action) {
        showDigitInputDialog(title.asString(context), hint, message.asString(context), range, requestKey)
      }
    }
  }
  private fun showInfoDialog(title: String, message: String) {
      InfoDialog.show(
          fragmentManager,
          title, message
      )
  }

  private fun showQuestionDialog(title: String, message: String, requestKey: String, hideCancel: Boolean = false, cancellable: Boolean = true) {
      QuestionDialog.show(fragmentManager, title, message, requestKey, hideCancel, cancellable)
  }

  private fun showDigitInputDialog(title: String, hint: String, message: String, range: IntRange, requestKey: String) {
      InputDigitDialog.show(fragmentManager, title, hint, message, range, requestKey)
  }
}
