package com.sonozaki.passwordsetup.presentation.states

import com.sonozaki.passwordstrength.PasswordStrengthData

sealed class SetupPasswordState(open val passwordStrength: PasswordStrengthData) {
  data class PasswordStrengthCheck(override val passwordStrength: PasswordStrengthData = PasswordStrengthData()): SetupPasswordState(passwordStrength)
  data class DisplayError(override val passwordStrength: PasswordStrengthData, val errorMessage: Int?): SetupPasswordState(passwordStrength)
}
