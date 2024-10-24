package com.android.aftools.presentation.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.android.aftools.R
import com.android.aftools.databinding.InfoDialogFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * DialogFragment for showing information to user
 */
class InfoDialog :
  DialogFragment() {


  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val args = requireArguments()
    val message = args.getString(MESSAGE) ?: throw RuntimeException("Message absent in InfoDialog")
    val title = args.getString(TITLE)?: throw RuntimeException("Title absent in InfoDialog")
    val infoDialogFragmentBinding = InfoDialogFragmentBinding.inflate(layoutInflater)
    infoDialogFragmentBinding.root.text = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    infoDialogFragmentBinding.root.movementMethod = LinkMovementMethod.getInstance()
    return MaterialAlertDialogBuilder(requireActivity())
      .setTitle(title)
      .setView(infoDialogFragmentBinding.root)
      .setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int -> dialog.cancel() }
      .create()
  }

  companion object {
    const val MESSAGE = "message"
    const val TITLE = "title"
    private const val TAG = "infoDialog"
    fun show(fragmentManager: FragmentManager, title: String, message: String) {
      val fragment = InfoDialog().apply {
        arguments = bundleOf(QuestionDialog.MESSAGE to message, QuestionDialog.TITLE to title)
      }
      fragment.show(fragmentManager, TAG)
    }


  }
}
