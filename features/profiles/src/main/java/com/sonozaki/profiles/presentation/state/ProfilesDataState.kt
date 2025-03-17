package com.sonozaki.profiles.presentation.state

import com.sonozaki.entities.ProfileDomain


sealed class ProfilesDataState {
  data object Loading : ProfilesDataState()
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileDomain>) : ProfilesDataState()
}
