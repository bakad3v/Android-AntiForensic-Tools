package com.android.aftools.presentation.bindingAdapters

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.android.aftools.presentation.states.ClassWithProgressBar
import com.android.aftools.presentation.states.ShowProgressBar

@BindingAdapter("controlProgressVisibility")
fun ProgressBar.controlProgressVisibility(state: ClassWithProgressBar) {
    this.visibility = if (state is ShowProgressBar) {
        View.VISIBLE
    } else {
        View.GONE
    }
}