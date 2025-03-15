package com.android.aftools.presentation.validators

import androidx.annotation.StringRes

data class ValidateResult(
  val isSuccess: Boolean,
  @StringRes val message: Int? = null
)
