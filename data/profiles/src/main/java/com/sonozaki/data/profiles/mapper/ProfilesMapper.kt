package com.sonozaki.data.profiles.mapper

import com.sonozaki.entities.ProfileDomain
import com.sonozaki.data.profiles.entities.IntList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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