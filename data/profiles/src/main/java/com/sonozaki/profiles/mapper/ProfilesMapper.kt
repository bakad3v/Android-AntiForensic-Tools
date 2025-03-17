package com.sonozaki.profiles.mapper

import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.entities.IntList
import javax.inject.Inject

class ProfilesMapper @Inject constructor() {

    fun mapToProfilesWithStatus(profiles: List<ProfileDomain>?, ids: IntList): List<ProfileDomain>? =
        profiles?.map {
            if (it.id in ids.list) {
                it.copy(toDelete = true)
            } else {
                it
            }
        }
}