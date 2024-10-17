package net.typeblog.shelter.presentation.bindingAdapters

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import net.typeblog.shelter.presentation.states.ClassWithProgressBar
import net.typeblog.shelter.presentation.states.ShowProgressBar

@BindingAdapter("controlProgressVisibility")
fun ProgressBar.controlProgressVisibility(state: ClassWithProgressBar) {
    this.visibility = if (state is ShowProgressBar) {
        View.VISIBLE
    } else {
        View.GONE
    }
}