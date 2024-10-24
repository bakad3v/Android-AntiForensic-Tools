package com.android.aftools.presentation.states

sealed class ActivityState {
  data object PasswordActivityState: ActivityState()
  data class NormalActivityState(val title: String): ActivityState()
}
