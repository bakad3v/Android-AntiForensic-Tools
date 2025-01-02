package net.typeblog.shelter.presentation.states

sealed class ActivityState {
  data object NoActionBarActivityState: ActivityState()
  data object NoActionBarNoDrawerActivityState: ActivityState()
  data class NormalActivityState(val title: String): ActivityState()
}
