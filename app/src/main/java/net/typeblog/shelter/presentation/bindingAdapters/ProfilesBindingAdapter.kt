package net.typeblog.shelter.presentation.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import net.typeblog.shelter.presentation.states.ProfilesDataState

@BindingAdapter("controlFilesVisibility")
fun RecyclerView.controlFilesVisibility(state: ProfilesDataState) {
    this.visibility = if (state is ProfilesDataState.Loading) {
        View.GONE
    } else {
        View.VISIBLE
    }
}