package net.typeblog.shelter.presentation.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import net.typeblog.shelter.R
import net.typeblog.shelter.presentation.states.PasswordState
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

