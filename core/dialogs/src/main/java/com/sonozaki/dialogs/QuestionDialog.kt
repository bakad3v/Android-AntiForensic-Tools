package com.sonozaki.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sonozaki.dialogs.databinding.QuestionDialogFragmentBinding


/**
 * Confirmation dialog fragment
 */
class QuestionDialog : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    with(requireArguments()) {
      val message = getString(MESSAGE)
        ?: throw RuntimeException("Message absent in QuestionDialog")
      val title = getString(TITLE)
        ?: throw RuntimeException("Title absent in QuestionDialog")
      val requestKey = getString(ARG_REQUEST_KEY)
        ?: throw RuntimeException("Request key absent in QuestionDialog")
      val hideCancel = getBoolean(HIDE_CANCEL)
      val cancellable = getBoolean(CANCELLABLE)
      val showDoNotShowAgain = getBoolean(SHOW_DO_NOT_SHOW_AGAIN)
      val binding = QuestionDialogFragmentBinding.inflate(layoutInflater)
      binding.message.text =
        HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
      binding.message.movementMethod = LinkMovementMethod.getInstance()
      binding.doNotShowAgain.isVisible = showDoNotShowAgain
      binding.doNotShowAgain.setOnCheckedChangeListener { _, isChecked ->
        parentFragmentManager.setFragmentResult(
          doNotShowAgainRequestKey(requestKey),
          bundleOf(RESPONSE to isChecked)
        )
      }
      val dialog = MaterialAlertDialogBuilder(requireActivity())
        .setTitle(title)
        .setView(binding.root)
        .setPositiveButton(R.string.ok) { _, _ ->
          parentFragmentManager.setFragmentResult(
            requestKey,
            bundleOf(RESPONSE to true)
          )
        }
      if (!cancellable) {
        isCancelable = false
      }
      if (!hideCancel) {
        dialog.setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
      }
      return dialog.create().apply {
        if (!cancellable) {
          setCanceledOnTouchOutside(false)
        }
      }
    }
  }

  companion object {
    const val MESSAGE = "message"
    const val TITLE = "title"
    private val TAG = QuestionDialog::class.simpleName
    private const val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"
    const val RESPONSE = "RESPONSE"
    const val HIDE_CANCEL = "HIDE_CANCEL"
    const val CANCELLABLE = "CANCELLABLE"
    private const val SHOW_DO_NOT_SHOW_AGAIN = "SHOW_DO_NOT_SHOW_AGAIN"
    fun show(
      fragmentManager: FragmentManager,
      title: String,
      message: String,
      requestKey: String,
      hideCancel: Boolean = false,
      cancellable: Boolean = true,
      showDoNotShowAgain: Boolean = false
    ) {
      val fragment = QuestionDialog().apply {
        arguments = bundleOf(
          MESSAGE to message,
          TITLE to title,
          ARG_REQUEST_KEY to requestKey,
          HIDE_CANCEL to hideCancel,
          CANCELLABLE to cancellable,
          SHOW_DO_NOT_SHOW_AGAIN to showDoNotShowAgain
        )
        }
      fragment.show(fragmentManager, TAG)
    }

    fun setupListener(fragmentManager: FragmentManager, requestKey: String, lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
      fragmentManager.setFragmentResultListener(requestKey,lifecycleOwner
      ) { _, _ -> listener.invoke() }
    }

    fun setupDoNotShowAgainListener(
      fragmentManager: FragmentManager,
      requestKey: String,
      lifecycleOwner: LifecycleOwner,
      listener: (Boolean) -> Unit
    ) {
      fragmentManager.setFragmentResultListener(
        doNotShowAgainRequestKey(requestKey),
        lifecycleOwner
      ) { _, result -> listener(result.getBoolean(RESPONSE)) }
    }

    private fun doNotShowAgainRequestKey(requestKey: String) =
      "${requestKey}_doNotShowAgain"

  }
}
