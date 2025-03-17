package com.sonozaki.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonozaki.dialogs.databinding.DigitInputDialogFragmentBinding
import com.sonozaki.validators.BaseValidator

/**
 * DialogFragment for requesting digital information from user
 */
class InputDigitDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message =
      arguments?.getString(MESSAGE) ?: throw RuntimeException("Message absent in InputDigitDialog")
    val title =
      arguments?.getString(TITLE) ?: throw RuntimeException("Title absent in InputDigitDialog")
    val hint =
      arguments?.getString(HINT) ?: throw RuntimeException("Hint absent in InputDigitDialog")
    val requestKey = arguments?.getString(ARG_REQUEST_KEY)
      ?: throw RuntimeException("Request key absent in InputDigitDialog")
    val minimum =
      arguments?.getInt(MINIMUM) ?: throw RuntimeException("Minimum absent in InputDigitDialog")
    val maximum =
      arguments?.getInt(MAXIMUM) ?: throw RuntimeException("Maximum absent in InputDigitDialog")
    val dialogBinding = DigitInputDialogFragmentBinding.inflate(layoutInflater)
    dialogBinding.inputEditText.hint = hint
    val dialog = MaterialAlertDialogBuilder(requireActivity())
      .setTitle(title)
      .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
      .setPositiveButton(R.string.ok, null)
      .setNegativeButton(R.string.cancel) { dialog: DialogInterface, i: Int -> dialog.cancel() }
      .setView(dialogBinding.root)
      .create()

    dialog.setOnShowListener {
      dialogBinding.inputEditText.requestFocus()
      showKeyboard(dialogBinding.inputEditText)

      dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
        val enteredText = dialogBinding.inputEditText.text.toString()
        val validation = BaseValidator.validate(DigitInBoundsValidator(enteredText, minimum, maximum))
        if (!validation.isSuccess) {
          dialogBinding.inputLayout.error = getString(validation.message!!)
          return@setOnClickListener
        }
        val text = enteredText.toIntOrNull()
        if (requestKey == EDIT_PRIORITY_REQUEST) {
          val fileUri = arguments?.getString(
            FILE_URI
          ) ?: throw RuntimeException("File Uri is not provided in priority editor")
          parentFragmentManager.setFragmentResult(
            requestKey,
            bundleOf(RESPONSE to text, FILE_URI to fileUri)
          )
        } else {
          parentFragmentManager.setFragmentResult(requestKey, bundleOf(RESPONSE to text))
        }
        dismiss()
      }
    }
    dialog.setOnDismissListener { hideKeyboard(dialogBinding.inputEditText) }
    return dialog
  }


  private fun showKeyboard(view: View) {
    view.post {
      getInputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
  }

  private fun hideKeyboard(view: View) {
    getInputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
  }

  private fun getInputMethodManager(view: View): InputMethodManager {
    val context = view.context
    return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  }

  companion object {
    const val MESSAGE = "message"
    const val TITLE = "title"
    const val HINT = "hint"
    private val TAG = InputDigitDialog::class.simpleName
    const val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"
    const val RESPONSE = "RESPONSE"
    const val EDIT_PRIORITY_REQUEST = "EDIT_PRIORITY_REQUEST"
    const val FILE_URI = "FILE_URI"
    private const val MINIMUM = "MINIMUM"
    private const val MAXIMUM = "MAXIMUM"
    fun show(
      fragmentManager: FragmentManager,
      title: String,
      hint: String,
      message: String,
      range: IntRange,
      requestKey: String
    ) {
      val fragment = InputDigitDialog().apply {
        arguments = bundleOf(
          TITLE to title,
          MESSAGE to message,
          HINT to hint,
          ARG_REQUEST_KEY to requestKey,
          MINIMUM to range.first,
          MAXIMUM to range.last
        )
      }
      fragment.show(fragmentManager, TAG)
    }

    fun showPriorityEditor(
      fragmentManager: FragmentManager,
      title: String,
      hint: String,
      message: String,
      uri: String,
      range: IntRange
    ) {
      val fragment = InputDigitDialog().apply {
        arguments = bundleOf(
          TITLE to title,
          MESSAGE to message,
          HINT to hint,
          FILE_URI to uri,
          ARG_REQUEST_KEY to EDIT_PRIORITY_REQUEST,
          MINIMUM to range.first,
          MAXIMUM to range.last
        )
      }
      fragment.show(fragmentManager, TAG)
    }

    fun setupEditPriorityListener(
      fragmentManager: FragmentManager,
      lifecycleOwner: LifecycleOwner,
      listener: (Uri, Int) -> Unit
    ) {
      fragmentManager.setFragmentResultListener(
        EDIT_PRIORITY_REQUEST, lifecycleOwner
      ) { _, result ->
        listener.invoke(
          Uri.parse(result.getString(FILE_URI)),
          result.getInt(RESPONSE)
        )
      }
    }

    fun setupListener(
      fragmentManager: FragmentManager,
      lifecycleOwner: LifecycleOwner,
      requestKey: String,
      listener: (Int) -> Unit
    ) {
      fragmentManager.setFragmentResultListener(
        requestKey, lifecycleOwner
      ) { _, result -> listener.invoke(result.getInt(RESPONSE)) }
    }

  }
}
