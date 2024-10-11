package com.android.aftools.presentation.states

sealed class PasswordState {
  data object CreatePassword: PasswordState()
  data object GetPassword: PasswordState()
  data class CheckPasswordResults(val rightPassword: Boolean): PasswordState()
}
