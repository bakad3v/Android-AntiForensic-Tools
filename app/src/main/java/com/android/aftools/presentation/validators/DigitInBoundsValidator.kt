package com.android.aftools.presentation.validators

import com.android.aftools.R

/**
 * User input validator for dialogs requiring digit input
 */
class DigitInBoundsValidator(private val digit: String,private val min: Int, private val max: Int):
  BaseValidator() {
  override fun validate(): ValidateResult {
    if (digit.isBlank()) {
      return(ValidateResult(false, R.string.empty_value_in_dialog))
    }
    val text = digit.toIntOrNull()
    if (text == null || text !in min..max){
      return(ValidateResult(false,R.string.number_out_of_range))
    }
    return ValidateResult(true)
  }
}
