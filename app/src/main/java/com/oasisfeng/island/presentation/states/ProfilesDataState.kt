package com.oasisfeng.island.presentation.states

import com.oasisfeng.island.domain.entities.ProfileDomain


sealed class ProfilesDataState: ClassWithProgressBar {
  data object Loading : ProfilesDataState(), ShowProgressBar
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileDomain>) : ProfilesDataState()
}
