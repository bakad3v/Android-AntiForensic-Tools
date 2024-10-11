package com.android.aftools.data.mappers

import android.os.Parcel
import android.os.UserHandle
import com.android.aftools.data.entities.IntList
import com.android.aftools.domain.entities.ProfileDomain
import javax.inject.Inject

class ProfilesMapper @Inject constructor() {

    fun mapUserHandleToProfile(userHandle: UserHandle): ProfileDomain {
        val parcel = Parcel.obtain()
        userHandle.writeToParcel(parcel,0)
        parcel.setDataPosition(0)
        val id = parcel.readInt()
        val main = id == 0
        return ProfileDomain(id,"Unknown name",main)
    }

    fun mapToProfilesWithStatus(profiles: List<ProfileDomain>?, ids: IntList): List<ProfileDomain>? =
        profiles?.map {
            if (it.id in ids.list) {
                it.copy(toDelete = true)
            } else {
                it
            }
        }

    fun mapIdToUserHandle(id: Int): UserHandle {
        val parcel = Parcel.obtain()
        parcel.writeInt(id)
        parcel.setDataPosition(0)
        val user = UserHandle.readFromParcel(parcel)
        parcel.recycle()
        return user
    }

    fun mapRootOutputToProfile(output: String) : ProfileDomain {
        val info = output.split("{")[1].split(":")
        val id = info[0].toInt()
        val name = info[1]
        return ProfileDomain(id = id,
            name = name,
            main = id == 0)
    }
}