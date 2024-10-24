package com.android.aftools.presentation.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.android.aftools.R
import com.android.aftools.presentation.states.PasswordState
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("wrongPasswordError")
fun TextInputLayout.wrongPasswordError(state: PasswordState) {
  if (state is PasswordState.CheckPasswordResults && !state.rightPassword) {
    error = context.getString(R.string.wrong_password)
  }
}

@BindingAdapter("passwordText")
fun TextView.passwordText(state: PasswordState) {
  text = with(context) {
    when (state) {
      is PasswordState.CreatePassword -> getString(R.string.create_password)
      else -> getString(R.string.enter_password)
    }
  }
}

