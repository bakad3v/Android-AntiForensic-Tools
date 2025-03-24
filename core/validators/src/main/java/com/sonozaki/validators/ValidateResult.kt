package com.sonozaki.validators

/**
 * Result of input validation
 */
data class ValidateResult(
  val isSuccess: Boolean,
  val message: Int? = null
)