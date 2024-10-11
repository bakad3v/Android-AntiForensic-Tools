package com.android.aftools.presentation.states

import com.android.aftools.domain.entities.ProfileDomain


sealed class ProfilesDataState {
  data object Loading : ProfilesDataState()
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileDomain>) : ProfilesDataState()
}
