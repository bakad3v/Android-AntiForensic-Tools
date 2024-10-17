package net.typeblog.shelter.presentation.states

import net.typeblog.shelter.domain.entities.ProfileDomain


sealed class ProfilesDataState: ClassWithProgressBar {
  data object Loading : ProfilesDataState(), ShowProgressBar
  data object SuperUserAbsent: ProfilesDataState()
  class ViewData(val items: List<ProfileDomain>) : ProfilesDataState()
}
