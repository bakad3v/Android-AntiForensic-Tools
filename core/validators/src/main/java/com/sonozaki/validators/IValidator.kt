package com.sonozaki.validators

interface IValidator {
  /**
   * Validate user input
   */
  fun validate() : ValidateResult
}
