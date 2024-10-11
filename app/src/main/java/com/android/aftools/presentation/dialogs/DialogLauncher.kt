package com.android.aftools.presentation.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.android.aftools.presentation.actions.DialogActions

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
      is DialogActions.ShowInputPasswordDialog -> with(action) {
        showPasswordInputDialog(title.asString(context),hint,message.asString(context))
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
    InputDigitDialog.show(fragmentManager,title,hint,message,range, requestKey)
  }

  private fun showSelectItemDialog(title: String, message: String, selected: Int, items: ArrayList<String>) {
    SelectItemDialog.show(fragmentManager, title, message, selected, items)
  }

  private fun showPasswordInputDialog(title: String, hint: String, message: String) {
    PasswordInputDialog.show(
      fragmentManager,
      title,
      hint,
      message
    )
  }
}
