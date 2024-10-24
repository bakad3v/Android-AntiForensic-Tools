package com.android.aftools.presentation.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.android.aftools.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SelectItemDialog: DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val selected = arguments?.getInt(SELECTED) ?: throw RuntimeException("Selected item absent in SelectItemDialog")
    val items = arguments?.getStringArrayList(ITEMS) ?: throw RuntimeException("Items absent in SelectItemDialog")
    val dialog = MaterialAlertDialogBuilder(requireContext())
      .setSingleChoiceItems(items.toTypedArray(),selected,null)
      .setPositiveButton(R.string.ok, null)
      .setNegativeButton(R.string.cancel) { dialog: DialogInterface, i: Int -> dialog.cancel() }
      .create()

    dialog.setOnShowListener {
      dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
        val index = dialog.listView.checkedItemPosition
        val item = items[index]
        parentFragmentManager.setFragmentResult(ARG_REQUEST_KEY, bundleOf(RESPONSE to item))
      }
    }
    return dialog
  }

  companion object {
    private const val MESSAGE = "message"
    private const val TITLE = "title"
    const val SELECTED = "selected"
    const val ITEMS = "items"
    const val RESPONSE = "RESPONSE"
    private const val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"
    private val TAG = QuestionDialog::class.simpleName
    fun show(fragmentManager: FragmentManager, title: String, message: String, selected: Int, items: ArrayList<String>) {
      val fragment = InputDigitDialog().apply {
        arguments = bundleOf(
          TITLE to title,
          MESSAGE to message, SELECTED to selected, ITEMS to items)
      }
      fragment.show(fragmentManager, TAG)
    }
    fun setupListener(fragmentManager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (String) -> Unit) {
      fragmentManager.setFragmentResultListener(
        ARG_REQUEST_KEY,lifecycleOwner
      ) { _, result -> listener.invoke(result.getString(RESPONSE)?:throw Exception("Response getting failed in SelectItemDialog")) }
    }
  }
}
