package net.typeblog.shelter.presentation.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import net.typeblog.shelter.presentation.states.DeletionDataState


@BindingAdapter("controlFilesVisibility")
fun RecyclerView.controlFilesVisibility(state: DeletionDataState) {
  this.visibility = if (state is DeletionDataState.Loading) {
    View.GONE
  } else {
    View.VISIBLE
  }
}

