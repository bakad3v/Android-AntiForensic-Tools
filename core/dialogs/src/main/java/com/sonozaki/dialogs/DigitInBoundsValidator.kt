package com.sonozaki.dialogs

import com.sonozaki.validators.BaseValidator
import com.sonozaki.validators.ValidateResult

/**
 * User input validator for dialogs requiring digit input. Asserts whether the number lies in specified bounds.
 */
class DigitInBoundsValidator(private val digit: String,private val min: Int, private val max: Int):
BaseValidator() {
  override fun validate(): ValidateResult {
    if (digit.isBlank()) {
      return(ValidateResult(false, R.string.empty_value_in_dialog))
    }
    val text = digit.toIntOrNull()
    if (text == null || text !in min..max){
      return(ValidateResult(false, R.string.number_out_of_range))
    }
    return ValidateResult(true)
  }
}
