package com.sonozaki.lockscreen.presentation.state

sealed class EnterPasswordState {
  data object Initial: EnterPasswordState()
  data object EnteringText: EnterPasswordState()
  data class CheckEnterPasswordResults(val rightPassword: Boolean): EnterPasswordState()
}
