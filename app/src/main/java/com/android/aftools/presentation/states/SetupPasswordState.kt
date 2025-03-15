package com.android.aftools.presentation.states

import com.android.aftools.passwordStrength.PasswordStrengthData
import com.android.aftools.presentation.validators.ValidateResult

sealed class SetupPasswordState(open val passwordStrength: PasswordStrengthData) {
  data class PasswordStrengthCheck(override val passwordStrength: PasswordStrengthData = PasswordStrengthData()): SetupPasswordState(passwordStrength)
  data class CheckEnterPasswordResults(override val passwordStrength: PasswordStrengthData, val validateResult: ValidateResult): SetupPasswordState(passwordStrength)
}
