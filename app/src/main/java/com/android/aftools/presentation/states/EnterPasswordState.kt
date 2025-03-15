package com.android.aftools.presentation.states

sealed class EnterPasswordState {
  data object Initial: EnterPasswordState()
  data object EnteringText: EnterPasswordState()
  data class CheckEnterPasswordResults(val rightPassword: Boolean): EnterPasswordState()
}
