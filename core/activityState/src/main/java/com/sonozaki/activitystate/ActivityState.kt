package com.sonozaki.activitystate

/**
 * Class for representing state of action bar and navigation drawer in app.
 */
sealed class ActivityState {
  data object NoActionBarActivityState: ActivityState()
  data object NoActionBarNoDrawerActivityState: ActivityState()
  data class NormalActivityState(val title: String): ActivityState()
}
