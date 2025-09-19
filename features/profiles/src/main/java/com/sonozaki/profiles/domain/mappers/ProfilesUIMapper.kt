package com.sonozaki.profiles.domain.mappers

import com.sonozaki.entities.Permissions
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.entities.ProfileUI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesUIMapper @Inject constructor() {
    fun map(profileDomain: ProfileDomain, permissions: Permissions): ProfileUI {
        if (permissions.isOwner || permissions.isRoot) {
            return ProfileUI(profileDomain, true)
        }
        return ProfileUI(profileDomain, false)
    }
}