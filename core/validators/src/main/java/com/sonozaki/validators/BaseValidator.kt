package com.sonozaki.validators

/**
 * Class for validating multiple constraints
 */
abstract class BaseValidator : IValidator {
  companion object {
    fun validate(vararg validators: IValidator): ValidateResult {
      validators.forEach {
        val result = it.validate()
        if (!result.isSuccess)
          return result
      }
      return ValidateResult(true)
    }
  }
}
