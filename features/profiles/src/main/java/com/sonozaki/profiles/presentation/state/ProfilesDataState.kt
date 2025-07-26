package com.sonozaki.profiles.presentation.state

import com.sonozaki.profiles.entities.ProfileUI


sealed class ProfilesDataState {
  data object Loading : ProfilesDataState()
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileUI>) : ProfilesDataState()
}
