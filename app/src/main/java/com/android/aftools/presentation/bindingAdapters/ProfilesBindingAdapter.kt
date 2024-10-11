package com.android.aftools.presentation.bindingAdapters

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.aftools.presentation.states.ProfilesDataState

@BindingAdapter("controlProgressVisibility")
fun ProgressBar.controlProgressVisibility(state: ProfilesDataState) {
    this.visibility = if (state is ProfilesDataState.Loading) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("controlFilesVisibility")
fun RecyclerView.controlFilesVisibility(state: ProfilesDataState) {
    this.visibility = if (state is ProfilesDataState.Loading) {
        View.GONE
    } else {
        View.VISIBLE
    }
}