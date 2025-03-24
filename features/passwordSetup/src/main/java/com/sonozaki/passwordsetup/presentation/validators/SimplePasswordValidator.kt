package com.sonozaki.passwordsetup.presentation.validators

import com.sonozaki.passwordsetup.R
import com.sonozaki.validators.BaseValidator
import com.sonozaki.validators.ValidateResult

/**
 * Simplified validator of user's given password
 */
class SimplePasswordValidator(private val password: String): BaseValidator() {
  override fun validate(): ValidateResult {
    if (password.length < MIN_PASS_LENGTH) {
      return ValidateResult(false, R.string.short_password)
    }
    return ValidateResult(true)
  }

  companion object {
    private const val MIN_PASS_LENGTH = 4
  }
}
