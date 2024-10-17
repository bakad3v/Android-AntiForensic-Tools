package com.android.aftools.presentation.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.android.aftools.presentation.states.RootState
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("visibilityFromRootState")
fun TextInputLayout.visibilityFromRootState(state: RootState) {
    visibility = when (state) {
        is RootState.Loading, RootState.NoRoot -> View.GONE
        else -> View.VISIBLE
    }
}

@BindingAdapter("enabledFromRootState")
fun TextInputEditText.enabledFromRootState(state: RootState) {
    isEnabled = when (state) {
        is RootState.Loading, RootState.ViewData, is RootState.LoadCommand, is RootState.NoRoot -> {
            false
        }
        is RootState.EditData -> {
            true
        }
    }
}