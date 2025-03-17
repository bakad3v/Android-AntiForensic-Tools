package com.sonozaki.passwordsetup.presentation.states

import com.sonozaki.passwordstrength.PasswordStrengthData

sealed class SetupPasswordState(open val passwordStrength: PasswordStrengthData) {
  data class PasswordStrengthCheck(override val passwordStrength: PasswordStrengthData = PasswordStrengthData()): SetupPasswordState(passwordStrength)
  data class CheckEnterPasswordResults(override val passwordStrength: PasswordStrengthData, val validateResult: com.sonozaki.validators.ValidateResult): SetupPasswordState(passwordStrength)
}
