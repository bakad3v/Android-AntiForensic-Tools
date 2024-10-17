package com.android.aftools.presentation.bindingAdapters

import android.view.View
import android.widget.ScrollView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.databinding.BindingAdapter
import com.android.aftools.presentation.states.LogsDataState
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("logsTextFromState")
fun MaterialTextView.textFromState(state: LogsDataState) {
  when (state) {
    is LogsDataState.Loading -> visibility = View.GONE
    is LogsDataState.ViewLogs -> {
      visibility = View.VISIBLE
      text = HtmlCompat.fromHtml(state.logs.asString(context), FROM_HTML_MODE_LEGACY)
    }
  }
}

@BindingAdapter("scrollOnNewItem")
fun ScrollView.scrollOnNewItem(state: LogsDataState) {
  if (state is LogsDataState.ViewLogs) {
    post {
      run {
        fullScroll(View.FOCUS_DOWN)
      }
    }
  }
}
