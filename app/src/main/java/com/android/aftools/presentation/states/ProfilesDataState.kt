package com.android.aftools.presentation.states

import com.android.aftools.domain.entities.ProfileDomain


sealed class ProfilesDataState: ClassWithProgressBar {
  data object Loading : ProfilesDataState(), ShowProgressBar
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileDomain>) : ProfilesDataState()
}
