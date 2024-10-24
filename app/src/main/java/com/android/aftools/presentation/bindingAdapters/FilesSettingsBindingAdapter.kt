package com.android.aftools.presentation.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.aftools.presentation.states.DeletionDataState


@BindingAdapter("controlFilesVisibility")
fun RecyclerView.controlFilesVisibility(state: DeletionDataState) {
  this.visibility = if (state is DeletionDataState.Loading) {
    View.GONE
  } else {
    View.VISIBLE
  }
}

